-- FSDAY / UT demo accounts and exploration data.
-- Login password for every seeded user: TenureTest!2026
-- Buyers : fsday.buyer01@test.tenure  ~ fsday.buyer10@test.tenure
-- Sellers: fsday.seller01@test.tenure ~ fsday.seller10@test.tenure
-- Reserved seed id range: 900000+

WITH seed(i) AS (
    SELECT generate_series(1, 10)
)
INSERT INTO users (
    id, email, password_hash, username, profile_image_url, gender, height_cm, weight_kg,
    grade, account_visibility, default_shipping_fee, default_fee_policy,
    default_purchase_offer_enabled, notification_settings, settlement_account,
    onboarding_completed, created_at, updated_at
)
SELECT
    900000 + i,
    'fsday.buyer' || lpad(i::text, 2, '0') || '@test.tenure',
    '$2a$10$mZcKOx9pawXr.AeU0V5Xre1s9xBq/DnxqJS1vNtt4sFBW1wFedFXm',
    'fs_buyer_' || lpad(i::text, 2, '0'),
    '/files/seed/profile-buyer-' || lpad(i::text, 2, '0') || '.jpg',
    CASE WHEN i % 2 = 0 THEN 'MALE' ELSE 'FEMALE' END,
    162 + i,
    52 + i,
    'BASIC',
    'PUBLIC',
    3000,
    'SELLER_PAYS',
    TRUE,
    '{"push":true,"trade":true,"chat":true,"interest":true}'::jsonb,
    NULL,
    TRUE,
    CURRENT_TIMESTAMP - (i || ' hours')::interval,
    CURRENT_TIMESTAMP - (i || ' hours')::interval
FROM seed;

WITH seed(i) AS (
    SELECT generate_series(1, 10)
)
INSERT INTO users (
    id, email, password_hash, username, profile_image_url, gender, height_cm, weight_kg,
    grade, account_visibility, default_shipping_fee, default_fee_policy,
    default_purchase_offer_enabled, notification_settings, settlement_account,
    onboarding_completed, created_at, updated_at
)
SELECT
    900100 + i,
    'fsday.seller' || lpad(i::text, 2, '0') || '@test.tenure',
    '$2a$10$mZcKOx9pawXr.AeU0V5Xre1s9xBq/DnxqJS1vNtt4sFBW1wFedFXm',
    'fs_seller_' || lpad(i::text, 2, '0'),
    '/files/seed/profile-seller-' || lpad(i::text, 2, '0') || '.jpg',
    CASE WHEN i % 2 = 0 THEN 'FEMALE' ELSE 'MALE' END,
    170 + i,
    60 + i,
    'RECORD',
    'PUBLIC',
    3000,
    'SELLER_PAYS',
    TRUE,
    '{"push":true,"trade":true,"chat":true,"interest":true}'::jsonb,
    '{"bankName":"Tenure Demo Bank","accountNumber":"fsday-demo","accountHolder":"FSDAY Seller"}'::jsonb,
    TRUE,
    CURRENT_TIMESTAMP - (i || ' hours')::interval,
    CURRENT_TIMESTAMP - (i || ' hours')::interval
FROM seed;

WITH seed(i) AS (
    SELECT generate_series(1, 10)
)
INSERT INTO delivery_addresses (
    id, user_id, receiver_name, phone, address_line1, address_line2,
    postal_code, request_note, is_default, created_at, updated_at
)
SELECT
    901000 + i,
    900000 + i,
    'FSDAY Buyer ' || lpad(i::text, 2, '0'),
    '010-1000-' || lpad(i::text, 4, '0'),
    'Seoul Dongjak-gu FSDAY-ro ' || i,
    'Buyer Test Room ' || lpad(i::text, 2, '0'),
    '070' || lpad(i::text, 2, '0'),
    'Leave at the door',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM seed;

WITH seed(i) AS (
    SELECT generate_series(1, 10)
)
INSERT INTO delivery_addresses (
    id, user_id, receiver_name, phone, address_line1, address_line2,
    postal_code, request_note, is_default, created_at, updated_at
)
SELECT
    901100 + i,
    900100 + i,
    'FSDAY Seller ' || lpad(i::text, 2, '0'),
    '010-2000-' || lpad(i::text, 4, '0'),
    'Seoul Mapo-gu Seller-ro ' || i,
    'Seller Test Studio ' || lpad(i::text, 2, '0'),
    '041' || lpad(i::text, 2, '0'),
    'Leave at reception',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM seed;

WITH seed(i) AS (
    SELECT generate_series(1, 10)
)
INSERT INTO items (
    id, owner_user_id, category_id, brand_name, item_name, wearing_target,
    size_system, size_value, representative_image_url, item_status,
    ootd_verified_wear_count, last_worn_at, first_owned_at, wish_count,
    purchase_offer_enabled, created_at, updated_at
)
SELECT
    902000 + i * 10 + 1,
    900100 + i,
    202,
    'FSDAY Denim',
    'Seller denim pants ' || lpad(i::text, 2, '0'),
    'UNISEX',
    'KR',
    'M',
    '/files/seed/item-seller-denim-' || lpad(i::text, 2, '0') || '.jpg',
    'ON_SALE',
    1,
    CURRENT_DATE - 1,
    CURRENT_DATE - 120,
    1,
    TRUE,
    CURRENT_TIMESTAMP - (20 + i || ' hours')::interval,
    CURRENT_TIMESTAMP - (20 + i || ' hours')::interval
FROM seed;

WITH seed(i) AS (
    SELECT generate_series(1, 10)
)
INSERT INTO items (
    id, owner_user_id, category_id, brand_name, item_name, wearing_target,
    size_system, size_value, representative_image_url, item_status,
    ootd_verified_wear_count, last_worn_at, first_owned_at, wish_count,
    purchase_offer_enabled, created_at, updated_at
)
SELECT
    902000 + i * 10 + 2,
    900100 + i,
    301,
    'FSDAY Outer',
    'Seller blouson jacket ' || lpad(i::text, 2, '0'),
    'UNISEX',
    'KR',
    'L',
    '/files/seed/item-seller-outer-' || lpad(i::text, 2, '0') || '.jpg',
    'ON_SALE',
    1,
    CURRENT_DATE - 2,
    CURRENT_DATE - 180,
    0,
    TRUE,
    CURRENT_TIMESTAMP - (19 + i || ' hours')::interval,
    CURRENT_TIMESTAMP - (19 + i || ' hours')::interval
FROM seed;

WITH seed(i) AS (
    SELECT generate_series(1, 10)
)
INSERT INTO items (
    id, owner_user_id, category_id, brand_name, item_name, wearing_target,
    size_system, size_value, representative_image_url, item_status,
    ootd_verified_wear_count, last_worn_at, first_owned_at, wish_count,
    purchase_offer_enabled, created_at, updated_at
)
SELECT
    902000 + i * 10 + 3,
    900100 + i,
    108,
    'FSDAY Hoodie',
    'Purchase offer hoodie ' || lpad(i::text, 2, '0'),
    'UNISEX',
    'KR',
    'L',
    '/files/seed/item-seller-hoodie-' || lpad(i::text, 2, '0') || '.jpg',
    'OWNED',
    1,
    CURRENT_DATE - 3,
    CURRENT_DATE - 240,
    0,
    TRUE,
    CURRENT_TIMESTAMP - (18 + i || ' hours')::interval,
    CURRENT_TIMESTAMP - (18 + i || ' hours')::interval
FROM seed;

WITH seed(i) AS (
    SELECT generate_series(1, 10)
)
INSERT INTO items (
    id, owner_user_id, category_id, brand_name, item_name, wearing_target,
    size_system, size_value, representative_image_url, item_status,
    ootd_verified_wear_count, last_worn_at, first_owned_at, wish_count,
    purchase_offer_enabled, created_at, updated_at
)
SELECT
    903000 + i,
    900000 + i,
    101,
    'FSDAY Tee',
    'OOTD tag test tee ' || lpad(i::text, 2, '0'),
    'UNISEX',
    'KR',
    'M',
    '/files/seed/item-buyer-tee-' || lpad(i::text, 2, '0') || '.jpg',
    'OWNED',
    1,
    CURRENT_DATE - 1,
    CURRENT_DATE - 90,
    0,
    TRUE,
    CURRENT_TIMESTAMP - (17 + i || ' hours')::interval,
    CURRENT_TIMESTAMP - (17 + i || ' hours')::interval
FROM seed;

INSERT INTO item_histories (
    id, item_id, owner_user_id, trade_id, acquisition_type, end_reason,
    started_at, ended_at, created_at
)
SELECT
    914000 + row_number() OVER (ORDER BY it.id),
    it.id,
    it.owner_user_id,
    NULL,
    'FIRST_REGISTERED',
    NULL,
    it.first_owned_at::timestamp,
    NULL::timestamp,
    it.created_at
FROM items it
WHERE it.id BETWEEN 902011 AND 903010;

WITH seed(i) AS (
    SELECT generate_series(1, 10)
)
INSERT INTO ootds (
    id, owner_user_id, image_url, source, tag_status, publication_status,
    review_required, tag_confirmed_at, heart_count, save_count, view_count,
    created_at, updated_at
)
SELECT
    905000 + i,
    900100 + i,
    '/files/seed/ootd-seller-' || lpad(i::text, 2, '0') || '.jpg',
    'CAMERA',
    'CONFIRMED',
    'ACTIVE',
    FALSE,
    CURRENT_TIMESTAMP - (12 + i || ' hours')::interval,
    1,
    1,
    20 + i,
    CURRENT_TIMESTAMP - (12 + i || ' hours')::interval,
    CURRENT_TIMESTAMP - (12 + i || ' hours')::interval
FROM seed;

WITH seed(i) AS (
    SELECT generate_series(1, 10)
)
INSERT INTO ootds (
    id, owner_user_id, image_url, source, tag_status, publication_status,
    review_required, tag_confirmed_at, heart_count, save_count, view_count,
    created_at, updated_at
)
SELECT
    905100 + i,
    900000 + i,
    '/files/seed/ootd-buyer-' || lpad(i::text, 2, '0') || '.jpg',
    'CAMERA',
    'CONFIRMED',
    'ACTIVE',
    FALSE,
    CURRENT_TIMESTAMP - (6 + i || ' hours')::interval,
    0,
    0,
    5 + i,
    CURRENT_TIMESTAMP - (6 + i || ' hours')::interval,
    CURRENT_TIMESTAMP - (6 + i || ' hours')::interval
FROM seed;

WITH seed(i) AS (
    SELECT generate_series(1, 10)
)
INSERT INTO ootd_tags (
    id, ootd_id, item_id, bbox_x, bbox_y, bbox_width, bbox_height,
    label_text, source, status, confidence, created_at, updated_at
)
SELECT 906000 + i * 10 + 1, 905000 + i, 902000 + i * 10 + 1,
       0.12000, 0.42000, 0.34000, 0.42000,
       'Sale denim pants', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM seed
UNION ALL
SELECT 906000 + i * 10 + 2, 905000 + i, 902000 + i * 10 + 2,
       0.15000, 0.08000, 0.36000, 0.30000,
       'Sale blouson jacket', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM seed
UNION ALL
SELECT 906000 + i * 10 + 3, 905100 + i, 903000 + i,
       0.18000, 0.12000, 0.32000, 0.36000,
       'Owned tee', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM seed;

WITH seed(i) AS (
    SELECT generate_series(1, 10)
)
INSERT INTO products (
    id, item_id, seller_user_id, price, shipping_fee, fee_policy, fee_rate,
    main_image_url, measurements, condition_flags, seller_description,
    product_status, created_at, updated_at
)
SELECT
    904000 + i * 10 + 1,
    902000 + i * 10 + 1,
    900100 + i,
    39000 + i * 1000,
    3000,
    'SELLER_PAYS',
    0.0600,
    '/files/seed/product-denim-' || lpad(i::text, 2, '0') || '.jpg',
    '{"waistWidth":38,"thighWidth":29,"totalLength":101,"rise":29,"inseam":73,"hemWidth":20}'::jsonb,
    '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb,
    'FSDAY direct purchase test product.',
    'ON_SALE',
    CURRENT_TIMESTAMP - (11 + i || ' hours')::interval,
    CURRENT_TIMESTAMP - (11 + i || ' hours')::interval
FROM seed
UNION ALL
SELECT
    904000 + i * 10 + 2,
    902000 + i * 10 + 2,
    900100 + i,
    59000 + i * 1000,
    0,
    'SPLIT',
    0.0600,
    '/files/seed/product-outer-' || lpad(i::text, 2, '0') || '.jpg',
    '{"shoulderWidth":48,"chestWidth":57,"sleeveLength":62,"totalLength":66}'::jsonb,
    '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb,
    'FSDAY exploration sale product.',
    'ON_SALE',
    CURRENT_TIMESTAMP - (10 + i || ' hours')::interval,
    CURRENT_TIMESTAMP - (10 + i || ' hours')::interval
FROM seed;

WITH seed(i) AS (
    SELECT generate_series(1, 10)
)
INSERT INTO product_attached_ootds (id, product_id, ootd_id, created_at)
SELECT 913000 + i * 10 + 1, 904000 + i * 10 + 1, 905000 + i, CURRENT_TIMESTAMP
FROM seed
UNION ALL
SELECT 913000 + i * 10 + 2, 904000 + i * 10 + 2, 905000 + i, CURRENT_TIMESTAMP
FROM seed;

WITH seed(i) AS (
    SELECT generate_series(1, 10)
)
INSERT INTO follow_relationships (
    id, follower_user_id, following_user_id, status, created_at, responded_at
)
SELECT
    909000 + i,
    900000 + i,
    900100 + i,
    'ACCEPTED',
    CURRENT_TIMESTAMP - (9 + i || ' hours')::interval,
    CURRENT_TIMESTAMP - (9 + i || ' hours')::interval
FROM seed;

WITH seed(i) AS (
    SELECT generate_series(1, 10)
)
INSERT INTO ootd_reactions (id, user_id, ootd_id, reaction_type, created_at)
SELECT 907000 + i * 10 + 1, 900000 + i, 905000 + i, 'HEART', CURRENT_TIMESTAMP
FROM seed
UNION ALL
SELECT 907000 + i * 10 + 2, 900000 + i, 905000 + i, 'SAVE', CURRENT_TIMESTAMP
FROM seed;

WITH seed(i) AS (
    SELECT generate_series(1, 10)
)
INSERT INTO wishes (
    id, user_id, item_id, notification_enabled, created_at, updated_at
)
SELECT
    908000 + i,
    900000 + i,
    902000 + i * 10 + 1,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM seed;

WITH seed(i) AS (
    SELECT generate_series(1, 10)
)
INSERT INTO recent_search_keywords (id, user_id, keyword, created_at)
SELECT 910000 + i, 900000 + i, 'denim', CURRENT_TIMESTAMP - (i || ' minutes')::interval
FROM seed;

WITH seed(i) AS (
    SELECT generate_series(1, 10)
)
INSERT INTO recent_viewed_users (id, viewer_user_id, viewed_user_id, last_viewed_at)
SELECT 910100 + i, 900000 + i, 900100 + i, CURRENT_TIMESTAMP - (i || ' minutes')::interval
FROM seed;

WITH seed(i) AS (
    SELECT generate_series(1, 10)
)
INSERT INTO recent_viewed_ootds (id, viewer_user_id, ootd_id, last_viewed_at)
SELECT 910200 + i, 900000 + i, 905000 + i, CURRENT_TIMESTAMP - (i || ' minutes')::interval
FROM seed;

WITH seed(i) AS (
    SELECT generate_series(1, 10)
)
INSERT INTO chat_rooms (
    id, item_id, buyer_user_id, seller_user_id, last_message, last_message_at,
    is_closed, created_at, updated_at
)
SELECT
    911000 + i,
    902000 + i * 10 + 1,
    900000 + i,
    900100 + i,
    'FSDAY seeded chat message.',
    CURRENT_TIMESTAMP - (i || ' minutes')::interval,
    FALSE,
    CURRENT_TIMESTAMP - (2 + i || ' hours')::interval,
    CURRENT_TIMESTAMP - (i || ' minutes')::interval
FROM seed;

WITH seed(i) AS (
    SELECT generate_series(1, 10)
)
INSERT INTO chat_messages (
    id, chat_room_id, sender_user_id, message_type, content, image_urls, created_at
)
SELECT
    911100 + i * 10 + 1,
    911000 + i,
    900000 + i,
    'TEXT',
    'Can I check the item condition?',
    NULL,
    CURRENT_TIMESTAMP - (2 + i || ' hours')::interval
FROM seed
UNION ALL
SELECT
    911100 + i * 10 + 2,
    911000 + i,
    900100 + i,
    'TEXT',
    'Yes, this seeded product is ready for FSDAY testing.',
    NULL,
    CURRENT_TIMESTAMP - (i || ' minutes')::interval
FROM seed;

WITH seed(i) AS (
    SELECT generate_series(1, 10)
)
INSERT INTO chat_room_members (
    id, chat_room_id, user_id, last_read_message_id, unread_count,
    is_exited, exited_at, created_at, updated_at
)
SELECT
    911300 + i * 10 + 1,
    911000 + i,
    900000 + i,
    911100 + i * 10 + 2,
    0,
    FALSE,
    NULL::timestamp,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM seed
UNION ALL
SELECT
    911300 + i * 10 + 2,
    911000 + i,
    900100 + i,
    911100 + i * 10 + 1,
    1,
    FALSE,
    NULL::timestamp,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM seed;

WITH seed(i) AS (
    SELECT generate_series(1, 10)
)
INSERT INTO notifications (
    id, receiver_user_id, category, type, body, target_type, target_id,
    read_at, image_url, brand_name, item_name, sender_username, created_at
)
SELECT
    912000 + i * 10 + 1,
    900100 + i,
    'NEEDS_ACTION',
    'CHAT_MESSAGE_CREATED',
    'Buyer sent a seeded chat message.',
    'CHAT',
    911000 + i,
    NULL::timestamp,
    '/files/seed/item-seller-denim-' || lpad(i::text, 2, '0') || '.jpg',
    'FSDAY Denim',
    'Seller denim pants ' || lpad(i::text, 2, '0'),
    'fs_buyer_' || lpad(i::text, 2, '0'),
    CURRENT_TIMESTAMP - (i || ' minutes')::interval
FROM seed
UNION ALL
SELECT
    912000 + i * 10 + 2,
    900000 + i,
    'ITEM_NEWS',
    'PRODUCT_CREATED',
    'A wished item is now on sale.',
    'ITEM',
    902000 + i * 10 + 1,
    NULL::timestamp,
    '/files/seed/item-seller-denim-' || lpad(i::text, 2, '0') || '.jpg',
    'FSDAY Denim',
    'Seller denim pants ' || lpad(i::text, 2, '0'),
    'fs_seller_' || lpad(i::text, 2, '0'),
    CURRENT_TIMESTAMP - (i || ' minutes')::interval
FROM seed;

SELECT setval(pg_get_serial_sequence('users', 'id'), GREATEST((SELECT MAX(id) FROM users), 900110), TRUE);
SELECT setval(pg_get_serial_sequence('delivery_addresses', 'id'), GREATEST((SELECT MAX(id) FROM delivery_addresses), 901110), TRUE);
SELECT setval(pg_get_serial_sequence('items', 'id'), GREATEST((SELECT MAX(id) FROM items), 903010), TRUE);
SELECT setval(pg_get_serial_sequence('item_histories', 'id'), GREATEST((SELECT MAX(id) FROM item_histories), 914040), TRUE);
SELECT setval(pg_get_serial_sequence('ootds', 'id'), GREATEST((SELECT MAX(id) FROM ootds), 905110), TRUE);
SELECT setval(pg_get_serial_sequence('ootd_tags', 'id'), GREATEST((SELECT MAX(id) FROM ootd_tags), 906103), TRUE);
SELECT setval(pg_get_serial_sequence('products', 'id'), GREATEST((SELECT MAX(id) FROM products), 904102), TRUE);
SELECT setval(pg_get_serial_sequence('product_attached_ootds', 'id'), GREATEST((SELECT MAX(id) FROM product_attached_ootds), 913102), TRUE);
SELECT setval(pg_get_serial_sequence('follow_relationships', 'id'), GREATEST((SELECT MAX(id) FROM follow_relationships), 909010), TRUE);
SELECT setval(pg_get_serial_sequence('ootd_reactions', 'id'), GREATEST((SELECT MAX(id) FROM ootd_reactions), 907102), TRUE);
SELECT setval(pg_get_serial_sequence('wishes', 'id'), GREATEST((SELECT MAX(id) FROM wishes), 908010), TRUE);
SELECT setval(pg_get_serial_sequence('recent_search_keywords', 'id'), GREATEST((SELECT MAX(id) FROM recent_search_keywords), 910010), TRUE);
SELECT setval(pg_get_serial_sequence('recent_viewed_users', 'id'), GREATEST((SELECT MAX(id) FROM recent_viewed_users), 910110), TRUE);
SELECT setval(pg_get_serial_sequence('recent_viewed_ootds', 'id'), GREATEST((SELECT MAX(id) FROM recent_viewed_ootds), 910210), TRUE);
SELECT setval(pg_get_serial_sequence('chat_rooms', 'id'), GREATEST((SELECT MAX(id) FROM chat_rooms), 911010), TRUE);
SELECT setval(pg_get_serial_sequence('chat_messages', 'id'), GREATEST((SELECT MAX(id) FROM chat_messages), 911202), TRUE);
SELECT setval(pg_get_serial_sequence('chat_room_members', 'id'), GREATEST((SELECT MAX(id) FROM chat_room_members), 911402), TRUE);
SELECT setval(pg_get_serial_sequence('notifications', 'id'), GREATEST((SELECT MAX(id) FROM notifications), 912102), TRUE);
