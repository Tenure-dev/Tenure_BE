-- Spread Ian seed OOTD tag boxes so mobile tag cards do not overlap.
-- The seed has exactly five tags per OOTD, with ids assigned sequentially.

UPDATE ootd_tags
SET
    bbox_x = CASE ((id - 926001) % 5)
        WHEN 0 THEN 0.06000
        WHEN 1 THEN 0.60000
        WHEN 2 THEN 0.08000
        WHEN 3 THEN 0.60000
        ELSE 0.32000
    END,
    bbox_y = CASE ((id - 926001) % 5)
        WHEN 0 THEN 0.10000
        WHEN 1 THEN 0.25000
        WHEN 2 THEN 0.44000
        WHEN 3 THEN 0.62000
        ELSE 0.79000
    END,
    bbox_width = CASE ((id - 926001) % 5)
        WHEN 4 THEN 0.26000
        ELSE 0.24000
    END,
    bbox_height = 0.10000,
    updated_at = CURRENT_TIMESTAMP
WHERE id BETWEEN 926001 AND 926250
  AND ootd_id BETWEEN 925001 AND 925050;
