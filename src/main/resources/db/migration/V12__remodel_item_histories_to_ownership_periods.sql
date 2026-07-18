ALTER TABLE item_histories
    ADD COLUMN acquisition_type VARCHAR(30),
    ADD COLUMN end_reason VARCHAR(30),
    ADD COLUMN started_at TIMESTAMP(6),
    ADD COLUMN ended_at TIMESTAMP(6);

ALTER TABLE item_histories RENAME COLUMN current_owner_user_id TO owner_user_id;
ALTER TABLE item_histories RENAME CONSTRAINT fk_item_histories_current_owner TO fk_item_histories_owner;

ALTER TABLE item_histories
    DROP COLUMN history_type,
    DROP COLUMN history_description;

UPDATE item_histories
SET acquisition_type = 'TENURE_TRADE',
    started_at = created_at
WHERE acquisition_type IS NULL;

UPDATE item_histories h
SET end_reason = 'TENURE_TRADE',
    ended_at = nxt.next_started_at
FROM (
    SELECT id,
           LEAD(started_at) OVER (PARTITION BY item_id ORDER BY started_at, id) AS next_started_at
    FROM item_histories
) nxt
WHERE h.id = nxt.id
  AND nxt.next_started_at IS NOT NULL;

INSERT INTO item_histories (item_id, owner_user_id, trade_id, acquisition_type, end_reason, started_at, ended_at, created_at)
SELECT earliest.item_id,
       earliest.previous_owner_user_id,
       NULL,
       'FIRST_REGISTERED',
       'TENURE_TRADE',
       COALESCE(it.first_owned_at::timestamp, it.created_at),
       earliest.started_at,
       earliest.started_at
FROM (
    SELECT DISTINCT ON (item_id) item_id, previous_owner_user_id, started_at
    FROM item_histories
    WHERE previous_owner_user_id IS NOT NULL
    ORDER BY item_id, started_at, id
) earliest
JOIN items it ON it.id = earliest.item_id;

INSERT INTO item_histories (item_id, owner_user_id, trade_id, acquisition_type, end_reason, started_at, ended_at, created_at)
SELECT it.id,
       it.owner_user_id,
       NULL,
       'FIRST_REGISTERED',
       NULL,
       COALESCE(it.first_owned_at::timestamp, it.created_at),
       NULL,
       it.created_at
FROM items it
WHERE NOT EXISTS (
    SELECT 1 FROM item_histories h WHERE h.item_id = it.id
);

ALTER TABLE item_histories
    DROP CONSTRAINT fk_item_histories_previous_owner,
    DROP CONSTRAINT fk_item_histories_ootd;

ALTER TABLE item_histories
    DROP COLUMN previous_owner_user_id,
    DROP COLUMN ootd_id;

ALTER TABLE item_histories
    ALTER COLUMN acquisition_type SET NOT NULL,
    ALTER COLUMN started_at SET NOT NULL;

ALTER TABLE item_histories
    ADD CONSTRAINT ck_item_histories_acquisition_type CHECK (
        acquisition_type IN ('FIRST_REGISTERED', 'TENURE_TRADE')
    ),
    ADD CONSTRAINT ck_item_histories_end_reason CHECK (
        end_reason IS NULL OR end_reason IN ('TENURE_TRADE', 'EXTERNAL_SALE')
    ),
    ADD CONSTRAINT ck_item_histories_end_consistency CHECK (
        (end_reason IS NULL AND ended_at IS NULL) OR (end_reason IS NOT NULL AND ended_at IS NOT NULL)
    );

CREATE UNIQUE INDEX uk_item_histories_item_open_row ON item_histories (item_id) WHERE ended_at IS NULL;

-- 불변식 확인 쿼리 (결과 0행이어야 정상)
-- 플랫폼 내 소유 상태(OWNED/ON_SALE 등)인 아이템만 열린 행이 정확히 1개여야 한다.
-- SOLD(외부 판매 완료), ARCHIVED(삭제) 상태는 열린 행이 0개인 게 정상이라 대상에서 제외한다.
-- SELECT i.id FROM items i
-- LEFT JOIN item_histories h ON h.item_id = i.id AND h.ended_at IS NULL
-- WHERE i.item_status NOT IN ('SOLD', 'ARCHIVED')
-- GROUP BY i.id HAVING COUNT(h.id) <> 1;
--
-- SELECT id FROM item_histories WHERE (end_reason IS NULL) <> (ended_at IS NULL);
--
-- SELECT id FROM item_histories WHERE acquisition_type NOT IN ('FIRST_REGISTERED', 'TENURE_TRADE');
