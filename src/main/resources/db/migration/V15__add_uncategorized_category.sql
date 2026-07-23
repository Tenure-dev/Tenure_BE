INSERT INTO categories (id, parent_id, name, depth, sort_order, is_active, created_at, updated_at)
VALUES
    (11, NULL, '미분류', 1, 11, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1101, 11, 'AI 분류 대기', 2, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

SELECT setval(
               pg_get_serial_sequence('categories', 'id'),
               (SELECT MAX(id) FROM categories)
       );
