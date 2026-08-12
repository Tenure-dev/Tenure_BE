-- Reset FSDAY demo relationship data and reseed with Ian OOTD sample data.
-- Login password for every seeded user remains: TenureTest!2026
-- Buyers : fsday.buyer01@test.tenure  ~ fsday.buyer10@test.tenure
-- Sellers: fsday.seller01@test.tenure ~ fsday.seller10@test.tenure

DELETE FROM notifications WHERE target_type IN ('OOTD', 'ITEM', 'PRODUCT', 'PURCHASE_INTENT', 'PURCHASE_OFFER', 'TRADE', 'CHAT');
DELETE FROM user_reports WHERE chat_room_id IS NOT NULL;
DELETE FROM product_reports;
DELETE FROM recent_viewed_ootds;
DELETE FROM wishes;
DELETE FROM ootd_reactions;
DELETE FROM product_attached_ootds;
DELETE FROM chat_room_members;
DELETE FROM chat_messages;
DELETE FROM chat_rooms;
DELETE FROM trades;
DELETE FROM purchase_intents;
DELETE FROM purchase_offers;
DELETE FROM item_histories;
DELETE FROM ootd_tags;
DELETE FROM products;
DELETE FROM items;
DELETE FROM ootds;

INSERT INTO ootds (id, owner_user_id, image_url, image_object_key, source, tag_status, publication_status, review_required, tag_confirmed_at, heart_count, save_count, view_count, hot_score, created_at, updated_at)
VALUES
    (925001, 900101, '/files/seed/ian/ootds/ootd_001.jpg', 'seed/ian/ootds/ootd_001.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '50 hours', 0, 0, 10, 0.0, CURRENT_TIMESTAMP - INTERVAL '50 hours', CURRENT_TIMESTAMP - INTERVAL '50 hours'),
    (925002, 900102, '/files/seed/ian/ootds/ootd_002.jpg', 'seed/ian/ootds/ootd_002.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '49 hours', 0, 0, 11, 0.0, CURRENT_TIMESTAMP - INTERVAL '49 hours', CURRENT_TIMESTAMP - INTERVAL '49 hours'),
    (925003, 900103, '/files/seed/ian/ootds/ootd_003.jpg', 'seed/ian/ootds/ootd_003.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '48 hours', 0, 0, 12, 0.0, CURRENT_TIMESTAMP - INTERVAL '48 hours', CURRENT_TIMESTAMP - INTERVAL '48 hours'),
    (925004, 900104, '/files/seed/ian/ootds/ootd_004.jpg', 'seed/ian/ootds/ootd_004.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '47 hours', 0, 0, 13, 0.0, CURRENT_TIMESTAMP - INTERVAL '47 hours', CURRENT_TIMESTAMP - INTERVAL '47 hours'),
    (925005, 900105, '/files/seed/ian/ootds/ootd_005.jpg', 'seed/ian/ootds/ootd_005.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '46 hours', 0, 0, 14, 0.0, CURRENT_TIMESTAMP - INTERVAL '46 hours', CURRENT_TIMESTAMP - INTERVAL '46 hours'),
    (925006, 900106, '/files/seed/ian/ootds/ootd_006.jpg', 'seed/ian/ootds/ootd_006.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '45 hours', 0, 0, 15, 0.0, CURRENT_TIMESTAMP - INTERVAL '45 hours', CURRENT_TIMESTAMP - INTERVAL '45 hours'),
    (925007, 900107, '/files/seed/ian/ootds/ootd_007.jpg', 'seed/ian/ootds/ootd_007.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '44 hours', 0, 0, 16, 0.0, CURRENT_TIMESTAMP - INTERVAL '44 hours', CURRENT_TIMESTAMP - INTERVAL '44 hours'),
    (925008, 900108, '/files/seed/ian/ootds/ootd_008.jpg', 'seed/ian/ootds/ootd_008.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '43 hours', 0, 0, 17, 0.0, CURRENT_TIMESTAMP - INTERVAL '43 hours', CURRENT_TIMESTAMP - INTERVAL '43 hours'),
    (925009, 900109, '/files/seed/ian/ootds/ootd_009.jpg', 'seed/ian/ootds/ootd_009.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '42 hours', 0, 0, 18, 0.0, CURRENT_TIMESTAMP - INTERVAL '42 hours', CURRENT_TIMESTAMP - INTERVAL '42 hours'),
    (925010, 900110, '/files/seed/ian/ootds/ootd_010.jpg', 'seed/ian/ootds/ootd_010.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '41 hours', 0, 0, 19, 0.0, CURRENT_TIMESTAMP - INTERVAL '41 hours', CURRENT_TIMESTAMP - INTERVAL '41 hours'),
    (925011, 900001, '/files/seed/ian/ootds/ootd_011.jpg', 'seed/ian/ootds/ootd_011.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '40 hours', 0, 0, 20, 0.0, CURRENT_TIMESTAMP - INTERVAL '40 hours', CURRENT_TIMESTAMP - INTERVAL '40 hours'),
    (925012, 900002, '/files/seed/ian/ootds/ootd_012.jpg', 'seed/ian/ootds/ootd_012.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '39 hours', 0, 0, 21, 0.0, CURRENT_TIMESTAMP - INTERVAL '39 hours', CURRENT_TIMESTAMP - INTERVAL '39 hours'),
    (925013, 900003, '/files/seed/ian/ootds/ootd_013.jpg', 'seed/ian/ootds/ootd_013.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '38 hours', 0, 0, 22, 0.0, CURRENT_TIMESTAMP - INTERVAL '38 hours', CURRENT_TIMESTAMP - INTERVAL '38 hours'),
    (925014, 900004, '/files/seed/ian/ootds/ootd_014.jpg', 'seed/ian/ootds/ootd_014.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '37 hours', 0, 0, 23, 0.0, CURRENT_TIMESTAMP - INTERVAL '37 hours', CURRENT_TIMESTAMP - INTERVAL '37 hours'),
    (925015, 900005, '/files/seed/ian/ootds/ootd_015.jpg', 'seed/ian/ootds/ootd_015.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '36 hours', 0, 0, 24, 0.0, CURRENT_TIMESTAMP - INTERVAL '36 hours', CURRENT_TIMESTAMP - INTERVAL '36 hours'),
    (925016, 900006, '/files/seed/ian/ootds/ootd_016.jpg', 'seed/ian/ootds/ootd_016.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '35 hours', 0, 0, 25, 0.0, CURRENT_TIMESTAMP - INTERVAL '35 hours', CURRENT_TIMESTAMP - INTERVAL '35 hours'),
    (925017, 900007, '/files/seed/ian/ootds/ootd_017.jpg', 'seed/ian/ootds/ootd_017.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '34 hours', 0, 0, 26, 0.0, CURRENT_TIMESTAMP - INTERVAL '34 hours', CURRENT_TIMESTAMP - INTERVAL '34 hours'),
    (925018, 900008, '/files/seed/ian/ootds/ootd_018.jpg', 'seed/ian/ootds/ootd_018.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '33 hours', 0, 0, 27, 0.0, CURRENT_TIMESTAMP - INTERVAL '33 hours', CURRENT_TIMESTAMP - INTERVAL '33 hours'),
    (925019, 900009, '/files/seed/ian/ootds/ootd_019.jpg', 'seed/ian/ootds/ootd_019.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '32 hours', 0, 0, 28, 0.0, CURRENT_TIMESTAMP - INTERVAL '32 hours', CURRENT_TIMESTAMP - INTERVAL '32 hours'),
    (925020, 900010, '/files/seed/ian/ootds/ootd_020.jpg', 'seed/ian/ootds/ootd_020.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '31 hours', 0, 0, 29, 0.0, CURRENT_TIMESTAMP - INTERVAL '31 hours', CURRENT_TIMESTAMP - INTERVAL '31 hours'),
    (925021, 900101, '/files/seed/ian/ootds/ootd_021.jpg', 'seed/ian/ootds/ootd_021.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '30 hours', 0, 0, 30, 0.0, CURRENT_TIMESTAMP - INTERVAL '30 hours', CURRENT_TIMESTAMP - INTERVAL '30 hours'),
    (925022, 900102, '/files/seed/ian/ootds/ootd_022.jpg', 'seed/ian/ootds/ootd_022.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '29 hours', 0, 0, 31, 0.0, CURRENT_TIMESTAMP - INTERVAL '29 hours', CURRENT_TIMESTAMP - INTERVAL '29 hours'),
    (925023, 900103, '/files/seed/ian/ootds/ootd_023.jpg', 'seed/ian/ootds/ootd_023.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '28 hours', 0, 0, 32, 0.0, CURRENT_TIMESTAMP - INTERVAL '28 hours', CURRENT_TIMESTAMP - INTERVAL '28 hours'),
    (925024, 900104, '/files/seed/ian/ootds/ootd_024.jpg', 'seed/ian/ootds/ootd_024.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '27 hours', 0, 0, 33, 0.0, CURRENT_TIMESTAMP - INTERVAL '27 hours', CURRENT_TIMESTAMP - INTERVAL '27 hours'),
    (925025, 900105, '/files/seed/ian/ootds/ootd_025.jpg', 'seed/ian/ootds/ootd_025.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '26 hours', 0, 0, 34, 0.0, CURRENT_TIMESTAMP - INTERVAL '26 hours', CURRENT_TIMESTAMP - INTERVAL '26 hours'),
    (925026, 900106, '/files/seed/ian/ootds/ootd_026.jpg', 'seed/ian/ootds/ootd_026.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '25 hours', 0, 0, 35, 0.0, CURRENT_TIMESTAMP - INTERVAL '25 hours', CURRENT_TIMESTAMP - INTERVAL '25 hours'),
    (925027, 900107, '/files/seed/ian/ootds/ootd_027.jpg', 'seed/ian/ootds/ootd_027.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '24 hours', 0, 0, 36, 0.0, CURRENT_TIMESTAMP - INTERVAL '24 hours', CURRENT_TIMESTAMP - INTERVAL '24 hours'),
    (925028, 900108, '/files/seed/ian/ootds/ootd_028.jpg', 'seed/ian/ootds/ootd_028.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '23 hours', 0, 0, 37, 0.0, CURRENT_TIMESTAMP - INTERVAL '23 hours', CURRENT_TIMESTAMP - INTERVAL '23 hours'),
    (925029, 900109, '/files/seed/ian/ootds/ootd_029.jpg', 'seed/ian/ootds/ootd_029.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '22 hours', 0, 0, 38, 0.0, CURRENT_TIMESTAMP - INTERVAL '22 hours', CURRENT_TIMESTAMP - INTERVAL '22 hours'),
    (925030, 900110, '/files/seed/ian/ootds/ootd_030.jpg', 'seed/ian/ootds/ootd_030.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '21 hours', 0, 0, 39, 0.0, CURRENT_TIMESTAMP - INTERVAL '21 hours', CURRENT_TIMESTAMP - INTERVAL '21 hours'),
    (925031, 900001, '/files/seed/ian/ootds/ootd_031.jpg', 'seed/ian/ootds/ootd_031.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '20 hours', 0, 0, 40, 0.0, CURRENT_TIMESTAMP - INTERVAL '20 hours', CURRENT_TIMESTAMP - INTERVAL '20 hours'),
    (925032, 900002, '/files/seed/ian/ootds/ootd_032.jpg', 'seed/ian/ootds/ootd_032.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '19 hours', 0, 0, 41, 0.0, CURRENT_TIMESTAMP - INTERVAL '19 hours', CURRENT_TIMESTAMP - INTERVAL '19 hours'),
    (925033, 900003, '/files/seed/ian/ootds/ootd_033.jpg', 'seed/ian/ootds/ootd_033.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '18 hours', 0, 0, 42, 0.0, CURRENT_TIMESTAMP - INTERVAL '18 hours', CURRENT_TIMESTAMP - INTERVAL '18 hours'),
    (925034, 900004, '/files/seed/ian/ootds/ootd_034.jpg', 'seed/ian/ootds/ootd_034.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '17 hours', 0, 0, 43, 0.0, CURRENT_TIMESTAMP - INTERVAL '17 hours', CURRENT_TIMESTAMP - INTERVAL '17 hours'),
    (925035, 900005, '/files/seed/ian/ootds/ootd_035.jpg', 'seed/ian/ootds/ootd_035.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '16 hours', 0, 0, 44, 0.0, CURRENT_TIMESTAMP - INTERVAL '16 hours', CURRENT_TIMESTAMP - INTERVAL '16 hours'),
    (925036, 900006, '/files/seed/ian/ootds/ootd_036.jpg', 'seed/ian/ootds/ootd_036.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '15 hours', 0, 0, 45, 0.0, CURRENT_TIMESTAMP - INTERVAL '15 hours', CURRENT_TIMESTAMP - INTERVAL '15 hours'),
    (925037, 900007, '/files/seed/ian/ootds/ootd_037.jpg', 'seed/ian/ootds/ootd_037.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '14 hours', 0, 0, 46, 0.0, CURRENT_TIMESTAMP - INTERVAL '14 hours', CURRENT_TIMESTAMP - INTERVAL '14 hours'),
    (925038, 900008, '/files/seed/ian/ootds/ootd_038.jpg', 'seed/ian/ootds/ootd_038.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '13 hours', 0, 0, 47, 0.0, CURRENT_TIMESTAMP - INTERVAL '13 hours', CURRENT_TIMESTAMP - INTERVAL '13 hours'),
    (925039, 900009, '/files/seed/ian/ootds/ootd_039.jpg', 'seed/ian/ootds/ootd_039.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '12 hours', 0, 0, 48, 0.0, CURRENT_TIMESTAMP - INTERVAL '12 hours', CURRENT_TIMESTAMP - INTERVAL '12 hours'),
    (925040, 900010, '/files/seed/ian/ootds/ootd_040.jpg', 'seed/ian/ootds/ootd_040.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '11 hours', 0, 0, 49, 0.0, CURRENT_TIMESTAMP - INTERVAL '11 hours', CURRENT_TIMESTAMP - INTERVAL '11 hours'),
    (925041, 900101, '/files/seed/ian/ootds/ootd_041.jpg', 'seed/ian/ootds/ootd_041.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '10 hours', 0, 0, 50, 0.0, CURRENT_TIMESTAMP - INTERVAL '10 hours', CURRENT_TIMESTAMP - INTERVAL '10 hours'),
    (925042, 900102, '/files/seed/ian/ootds/ootd_042.jpg', 'seed/ian/ootds/ootd_042.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '9 hours', 0, 0, 51, 0.0, CURRENT_TIMESTAMP - INTERVAL '9 hours', CURRENT_TIMESTAMP - INTERVAL '9 hours'),
    (925043, 900103, '/files/seed/ian/ootds/ootd_043.jpg', 'seed/ian/ootds/ootd_043.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '8 hours', 0, 0, 52, 0.0, CURRENT_TIMESTAMP - INTERVAL '8 hours', CURRENT_TIMESTAMP - INTERVAL '8 hours'),
    (925044, 900104, '/files/seed/ian/ootds/ootd_044.jpg', 'seed/ian/ootds/ootd_044.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '7 hours', 0, 0, 53, 0.0, CURRENT_TIMESTAMP - INTERVAL '7 hours', CURRENT_TIMESTAMP - INTERVAL '7 hours'),
    (925045, 900105, '/files/seed/ian/ootds/ootd_045.jpg', 'seed/ian/ootds/ootd_045.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '6 hours', 0, 0, 54, 0.0, CURRENT_TIMESTAMP - INTERVAL '6 hours', CURRENT_TIMESTAMP - INTERVAL '6 hours'),
    (925046, 900106, '/files/seed/ian/ootds/ootd_046.webp', 'seed/ian/ootds/ootd_046.webp', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '5 hours', 0, 0, 55, 0.0, CURRENT_TIMESTAMP - INTERVAL '5 hours', CURRENT_TIMESTAMP - INTERVAL '5 hours'),
    (925047, 900107, '/files/seed/ian/ootds/ootd_047.webp', 'seed/ian/ootds/ootd_047.webp', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '4 hours', 0, 0, 56, 0.0, CURRENT_TIMESTAMP - INTERVAL '4 hours', CURRENT_TIMESTAMP - INTERVAL '4 hours'),
    (925048, 900108, '/files/seed/ian/ootds/ootd_048.jpg', 'seed/ian/ootds/ootd_048.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '3 hours', 0, 0, 57, 0.0, CURRENT_TIMESTAMP - INTERVAL '3 hours', CURRENT_TIMESTAMP - INTERVAL '3 hours'),
    (925049, 900109, '/files/seed/ian/ootds/ootd_049.jpg', 'seed/ian/ootds/ootd_049.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '2 hours', 0, 0, 58, 0.0, CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '2 hours'),
    (925050, 900110, '/files/seed/ian/ootds/ootd_050.jpg', 'seed/ian/ootds/ootd_050.jpg', 'CAMERA', 'CONFIRMED', 'ACTIVE', FALSE, CURRENT_TIMESTAMP - INTERVAL '1 hours', 0, 0, 59, 0.0, CURRENT_TIMESTAMP - INTERVAL '1 hours', CURRENT_TIMESTAMP - INTERVAL '1 hours');

INSERT INTO items (id, owner_user_id, category_id, brand_name, item_name, wearing_target, size_system, size_value, representative_image_url, representative_image_object_key, item_status, ootd_verified_wear_count, last_worn_at, first_owned_at, wish_count, purchase_offer_enabled, created_at, updated_at)
VALUES
    (920001, 900101, 303, 'Levi''s', '빈티지 블루 트러커 데님 재킷', 'MENSWEAR', 'KR', '2', '/files/seed/ian/products/ootd_001_item_01.jpg', 'seed/ian/products/ootd_001_item_01.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-01-18'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '300 minutes', CURRENT_TIMESTAMP - INTERVAL '300 minutes'),
    (920002, 900101, 602, 'Polo Ralph Lauren', '브라운 체크 울 머플러', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-09-24'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '299 minutes', CURRENT_TIMESTAMP - INTERVAL '299 minutes'),
    (920003, 900101, 201, 'Carhartt WIP', '브라운 워크 코튼 팬츠', 'MENSWEAR', 'KR', '44', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-04-14'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '298 minutes', CURRENT_TIMESTAMP - INTERVAL '298 minutes'),
    (920004, 900101, 506, 'Red Wing', '브라운 라운드 워크 부츠', 'MENSWEAR', 'KR', '280', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-12-22'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '297 minutes', CURRENT_TIMESTAMP - INTERVAL '297 minutes'),
    (920005, 900101, 401, 'Patagonia', '카모플라주 로고 볼캡', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_001_item_05.jpg', 'seed/ian/products/ootd_001_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-09-20'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '296 minutes', CURRENT_TIMESTAMP - INTERVAL '296 minutes'),
    (920006, 900102, 302, 'Acne Studios', '브라운 디스트로이드 레더 재킷', 'WOMENSWEAR', 'KR', '85', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-10-04'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '295 minutes', CURRENT_TIMESTAMP - INTERVAL '295 minutes'),
    (920007, 900102, 109, 'COS', '라임 그린 크루넥 니트', 'WOMENSWEAR', 'KR', '2', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-04-02'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '294 minutes', CURRENT_TIMESTAMP - INTERVAL '294 minutes'),
    (920008, 900102, 202, 'Levi''s', '다크 인디고 와이드 데님 팬츠', 'WOMENSWEAR', 'KR', '38', '/files/seed/ian/products/ootd_002_item_03.jpg', 'seed/ian/products/ootd_002_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-05-31'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '293 minutes', CURRENT_TIMESTAMP - INTERVAL '293 minutes'),
    (920009, 900102, 702, 'Lemaire', '블랙 소프트 숄더백', 'WOMENSWEAR', 'KR', 'One size', '/files/seed/ian/products/ootd_002_item_04.jpg', 'seed/ian/products/ootd_002_item_04.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-10-08'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '292 minutes', CURRENT_TIMESTAMP - INTERVAL '292 minutes'),
    (920010, 900102, 505, 'Dr. Martens', '브라운 레더 부츠', 'WOMENSWEAR', 'KR', '215', '/files/seed/ian/products/ootd_002_item_05.jpg', 'seed/ian/products/ootd_002_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-12-11'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '291 minutes', CURRENT_TIMESTAMP - INTERVAL '291 minutes'),
    (920011, 900103, 109, 'COS', '퍼플 브이넥 오버핏 니트', 'WOMENSWEAR', 'KR', '38', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-10-04'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '290 minutes', CURRENT_TIMESTAMP - INTERVAL '290 minutes'),
    (920012, 900103, 104, 'MUJI', '차콜 체크 레이어드 셔츠', 'WOMENSWEAR', 'KR', 'L', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-11-14'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '289 minutes', CURRENT_TIMESTAMP - INTERVAL '289 minutes'),
    (920013, 900103, 206, 'Theory', '차콜 와이드 트라우저', 'WOMENSWEAR', 'KR', '38', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-05-13'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '288 minutes', CURRENT_TIMESTAMP - INTERVAL '288 minutes'),
    (920014, 900103, 702, 'A.P.C.', '브라운 레더 숄더백', 'WOMENSWEAR', 'KR', 'One size', '/files/seed/ian/products/ootd_003_item_04.jpg', 'seed/ian/products/ootd_003_item_04.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-10-17'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '287 minutes', CURRENT_TIMESTAMP - INTERVAL '287 minutes'),
    (920015, 900103, 503, 'Camper', '블랙 라운드 토 슈즈', 'WOMENSWEAR', 'KR', '215', '/files/seed/ian/products/ootd_003_item_05.jpg', 'seed/ian/products/ootd_003_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-11-20'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '286 minutes', CURRENT_TIMESTAMP - INTERVAL '286 minutes'),
    (920016, 900104, 309, 'Patagonia', '블루 신칠라 플리스 재킷', 'WOMENSWEAR', 'KR', '36', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-08-02'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '285 minutes', CURRENT_TIMESTAMP - INTERVAL '285 minutes'),
    (920017, 900104, 110, 'Ralph Lauren', '올리브 코튼 가디건', 'WOMENSWEAR', 'KR', 'L', '/files/seed/ian/products/ootd_004_item_02.jpg', 'seed/ian/products/ootd_004_item_02.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-01-08'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '284 minutes', CURRENT_TIMESTAMP - INTERVAL '284 minutes'),
    (920018, 900104, 902, 'COS', '오프화이트 플레어 미디 스커트', 'WOMENSWEAR', 'KR', 'XS', '/files/seed/ian/products/ootd_004_item_03.jpg', 'seed/ian/products/ootd_004_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-11-23'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '283 minutes', CURRENT_TIMESTAMP - INTERVAL '283 minutes'),
    (920019, 900104, 504, 'Dr. Martens', '블랙 1461 더비 슈즈', 'WOMENSWEAR', 'KR', '210', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-02-24'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '282 minutes', CURRENT_TIMESTAMP - INTERVAL '282 minutes'),
    (920020, 900104, 702, 'Lemaire', '블랙 미니 숄더백', 'WOMENSWEAR', 'KR', 'One size', '/files/seed/ian/products/ootd_004_item_05.jpg', 'seed/ian/products/ootd_004_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-06-15'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '281 minutes', CURRENT_TIMESTAMP - INTERVAL '281 minutes'),
    (920021, 900105, 311, 'Carhartt WIP', '브라운 퀼팅 워크 재킷', 'MENSWEAR', 'KR', '2XL', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-09-25'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '280 minutes', CURRENT_TIMESTAMP - INTERVAL '280 minutes'),
    (920022, 900105, 108, 'Champion', '머스터드 레이어드 후디', 'MENSWEAR', 'KR', '44', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-03-29'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '279 minutes', CURRENT_TIMESTAMP - INTERVAL '279 minutes'),
    (920023, 900105, 201, 'Dickies', '네이비 와이드 워크 팬츠', 'MENSWEAR', 'KR', 'XL', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-11-20'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '278 minutes', CURRENT_TIMESTAMP - INTERVAL '278 minutes'),
    (920024, 900105, 401, 'New Era', '블루 NY 로고 볼캡', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-01-01'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '277 minutes', CURRENT_TIMESTAMP - INTERVAL '277 minutes'),
    (920025, 900105, 501, 'Vans', '블랙 올드스쿨 스니커즈', 'UNISEX', 'KR', '240', '/files/seed/ian/products/ootd_005_item_05.jpg', 'seed/ian/products/ootd_005_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-03-01'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '276 minutes', CURRENT_TIMESTAMP - INTERVAL '276 minutes'),
    (920026, 900106, 104, 'Polo Ralph Lauren', '레드 깅엄 체크 셔츠', 'MENSWEAR', 'KR', '95', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-05-13'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '275 minutes', CURRENT_TIMESTAMP - INTERVAL '275 minutes'),
    (920027, 900106, 101, 'Hanes', '화이트 크루넥 반팔 티셔츠', 'MENSWEAR', 'KR', '110', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-04-16'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '274 minutes', CURRENT_TIMESTAMP - INTERVAL '274 minutes'),
    (920028, 900106, 201, 'Carhartt WIP', '베이지 와이드 코튼 팬츠', 'MENSWEAR', 'KR', 'M', '/files/seed/ian/products/ootd_006_item_03.jpg', 'seed/ian/products/ootd_006_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-03-03'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '273 minutes', CURRENT_TIMESTAMP - INTERVAL '273 minutes'),
    (920029, 900106, 504, 'Dr. Martens', '블랙 스퀘어 토 더비 슈즈', 'MENSWEAR', 'KR', '285', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-06-23'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '272 minutes', CURRENT_TIMESTAMP - INTERVAL '272 minutes'),
    (920030, 900106, 402, 'KANGOL', '멀티컬러 크로셰 비니', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_006_item_05.jpg', 'seed/ian/products/ootd_006_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-07-26'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '271 minutes', CURRENT_TIMESTAMP - INTERVAL '271 minutes'),
    (920031, 900107, 109, 'COS', '코발트 블루 크루넥 니트', 'WOMENSWEAR', 'KR', '90', '/files/seed/ian/products/ootd_007_item_01.jpg', 'seed/ian/products/ootd_007_item_01.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-06-10'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '270 minutes', CURRENT_TIMESTAMP - INTERVAL '270 minutes'),
    (920032, 900107, 201, 'UNIQLO U', '베이지 와이드 코튼 팬츠', 'WOMENSWEAR', 'KR', 'S', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-01-28'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '269 minutes', CURRENT_TIMESTAMP - INTERVAL '269 minutes'),
    (920033, 900107, 602, 'Polo Ralph Lauren', '네이비 울 머플러', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-01-05'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '268 minutes', CURRENT_TIMESTAMP - INTERVAL '268 minutes'),
    (920034, 900107, 604, 'THE NORTH FACE', '블랙 스트라이프 글러브', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_007_item_04.jpg', 'seed/ian/products/ootd_007_item_04.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-05-01'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '267 minutes', CURRENT_TIMESTAMP - INTERVAL '267 minutes'),
    (920035, 900107, 503, 'Camper', '다크 브라운 플랫 슈즈', 'WOMENSWEAR', 'KR', '235', '/files/seed/ian/products/ootd_007_item_05.jpg', 'seed/ian/products/ootd_007_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-10-31'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '266 minutes', CURRENT_TIMESTAMP - INTERVAL '266 minutes'),
    (920036, 900108, 304, 'Patagonia', '레드 토렌쉘 윈드브레이커', 'MENSWEAR', 'KR', 'M', '/files/seed/ian/products/ootd_008_item_01.jpg', 'seed/ian/products/ootd_008_item_01.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-05-14'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '265 minutes', CURRENT_TIMESTAMP - INTERVAL '265 minutes'),
    (920037, 900108, 202, 'Levi''s', '다크 인디고 와이드 데님 팬츠', 'MENSWEAR', 'KR', 'XL', '/files/seed/ian/products/ootd_008_item_02.jpg', 'seed/ian/products/ootd_008_item_02.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-12-05'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '264 minutes', CURRENT_TIMESTAMP - INTERVAL '264 minutes'),
    (920038, 900108, 402, 'Stussy', '로열 블루 로고 비니', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_008_item_03.jpg', 'seed/ian/products/ootd_008_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-11-06'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '263 minutes', CURRENT_TIMESTAMP - INTERVAL '263 minutes'),
    (920039, 900108, 606, 'Oakley', '블랙 스퀘어 선글라스', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-03-25'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '262 minutes', CURRENT_TIMESTAMP - INTERVAL '262 minutes'),
    (920040, 900108, 809, 'Chrome Hearts', '실버 월렛 체인', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-10-09'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '261 minutes', CURRENT_TIMESTAMP - INTERVAL '261 minutes'),
    (920041, 900109, 304, 'Arc''teryx', '블루 베타 쉘 재킷', 'WOMENSWEAR', 'KR', '1', '/files/seed/ian/products/ootd_009_item_01.jpg', 'seed/ian/products/ootd_009_item_01.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-11-15'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '260 minutes', CURRENT_TIMESTAMP - INTERVAL '260 minutes'),
    (920042, 900109, 202, 'Levi''s', '다크 워시 데님 팬츠', 'WOMENSWEAR', 'KR', '38', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-11-11'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '259 minutes', CURRENT_TIMESTAMP - INTERVAL '259 minutes'),
    (920043, 900109, 606, 'Oakley', '블랙 실드 선글라스', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_009_item_03.jpg', 'seed/ian/products/ootd_009_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-07-12'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '258 minutes', CURRENT_TIMESTAMP - INTERVAL '258 minutes'),
    (920044, 900109, 703, 'PORTER', '블랙 나일론 크로스백', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-12-06'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '257 minutes', CURRENT_TIMESTAMP - INTERVAL '257 minutes'),
    (920045, 900109, 802, 'Maison Margiela', '실버 볼드 링', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_009_item_05.jpg', 'seed/ian/products/ootd_009_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-10-26'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '256 minutes', CURRENT_TIMESTAMP - INTERVAL '256 minutes'),
    (920046, 900110, 102, 'UNIQLO U', '그레이 와플 긴팔 티셔츠', 'WOMENSWEAR', 'KR', '38', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-07-19'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '255 minutes', CURRENT_TIMESTAMP - INTERVAL '255 minutes'),
    (920047, 900110, 202, 'Levi''s', '라이트 블루 와이드 데님 팬츠', 'WOMENSWEAR', 'KR', 'XS', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-01-18'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '254 minutes', CURRENT_TIMESTAMP - INTERVAL '254 minutes'),
    (920048, 900110, 402, 'Stussy', '스카이 블루 로고 비니', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-02-17'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '253 minutes', CURRENT_TIMESTAMP - INTERVAL '253 minutes'),
    (920049, 900110, 601, 'A.P.C.', '블랙 웨스턴 레더 벨트', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_010_item_04.jpg', 'seed/ian/products/ootd_010_item_04.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-01-22'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '252 minutes', CURRENT_TIMESTAMP - INTERVAL '252 minutes'),
    (920050, 900110, 801, 'Vivienne Westwood', '레드 비즈 펜던트 목걸이', 'WOMENSWEAR', 'KR', 'One size', '/files/seed/ian/products/ootd_010_item_05.jpg', 'seed/ian/products/ootd_010_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-01-09'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '251 minutes', CURRENT_TIMESTAMP - INTERVAL '251 minutes'),
    (920051, 900001, 109, 'Miu Miu', '레드 피티드 반팔 니트', 'WOMENSWEAR', 'KR', '2', '/files/seed/ian/products/ootd_011_item_01.jpg', 'seed/ian/products/ootd_011_item_01.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-04-11'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '250 minutes', CURRENT_TIMESTAMP - INTERVAL '250 minutes'),
    (920052, 900001, 202, 'Levi''s', '라이트 블루 와이드 데님 팬츠', 'WOMENSWEAR', 'KR', '0', '/files/seed/ian/products/ootd_011_item_02.jpg', 'seed/ian/products/ootd_011_item_02.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-04-22'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '249 minutes', CURRENT_TIMESTAMP - INTERVAL '249 minutes'),
    (920053, 900001, 402, 'KANGOL', '멀티컬러 크로셰 비니', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_011_item_03.jpg', 'seed/ian/products/ootd_011_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-05-12'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '248 minutes', CURRENT_TIMESTAMP - INTERVAL '248 minutes'),
    (920054, 900001, 601, 'A.P.C.', '오프화이트 브레이디드 벨트', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_011_item_04.jpg', 'seed/ian/products/ootd_011_item_04.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-05-21'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '247 minutes', CURRENT_TIMESTAMP - INTERVAL '247 minutes'),
    (920055, 900001, 503, 'Clarks', '브라운 레더 슈즈', 'WOMENSWEAR', 'KR', '215', '/files/seed/ian/products/ootd_011_item_05.jpg', 'seed/ian/products/ootd_011_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-07-27'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '246 minutes', CURRENT_TIMESTAMP - INTERVAL '246 minutes'),
    (920056, 900002, 109, 'KAPITAL', '그린 그래픽 자카드 니트', 'UNISEX', 'KR', 'S', '/files/seed/ian/products/ootd_012_item_01.jpg', 'seed/ian/products/ootd_012_item_01.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-07-17'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '245 minutes', CURRENT_TIMESTAMP - INTERVAL '245 minutes'),
    (920057, 900002, 402, 'KANGOL', '멀티컬러 핸드메이드 비니', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-09-16'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '244 minutes', CURRENT_TIMESTAMP - INTERVAL '244 minutes'),
    (920058, 900002, 602, 'KAPITAL', '라임 페이즐리 스카프', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-12-05'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '243 minutes', CURRENT_TIMESTAMP - INTERVAL '243 minutes'),
    (920059, 900002, 201, 'Dickies', '오프화이트 와이드 코튼 팬츠', 'UNISEX', 'KR', '44', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-08-21'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '242 minutes', CURRENT_TIMESTAMP - INTERVAL '242 minutes'),
    (920060, 900002, 501, 'Nike', '그린 에어맥스 스니커즈', 'UNISEX', 'KR', '260', '/files/seed/ian/products/ootd_012_item_05.jpg', 'seed/ian/products/ootd_012_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-05-28'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '241 minutes', CURRENT_TIMESTAMP - INTERVAL '241 minutes'),
    (920061, 900003, 109, 'Acne Studios', '그레이 브러시드 모헤어 니트', 'WOMENSWEAR', 'KR', '1', '/files/seed/ian/products/ootd_013_item_01.jpg', 'seed/ian/products/ootd_013_item_01.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-09-26'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '240 minutes', CURRENT_TIMESTAMP - INTERVAL '240 minutes'),
    (920062, 900003, 602, 'Polo Ralph Lauren', '블루 레드 스트라이프 머플러', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_013_item_02.jpg', 'seed/ian/products/ootd_013_item_02.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-11-29'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '239 minutes', CURRENT_TIMESTAMP - INTERVAL '239 minutes'),
    (920063, 900003, 202, 'Levi''s', '미드 블루 와이드 데님 팬츠', 'WOMENSWEAR', 'KR', 'S', '/files/seed/ian/products/ootd_013_item_03.jpg', 'seed/ian/products/ootd_013_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-03-06'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '238 minutes', CURRENT_TIMESTAMP - INTERVAL '238 minutes'),
    (920064, 900003, 503, 'L.L.Bean', '브라운 모카신 슈즈', 'WOMENSWEAR', 'KR', '235', '/files/seed/ian/products/ootd_013_item_04.jpg', 'seed/ian/products/ootd_013_item_04.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-03-20'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '237 minutes', CURRENT_TIMESTAMP - INTERVAL '237 minutes'),
    (920065, 900003, 606, 'Gentle Monster', '실버 라운드 안경', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-12-15'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '236 minutes', CURRENT_TIMESTAMP - INTERVAL '236 minutes'),
    (920066, 900004, 101, 'Stussy', '레드 그래픽 반팔 티셔츠', 'MENSWEAR', 'KR', '46', '/files/seed/ian/products/ootd_014_item_01.jpg', 'seed/ian/products/ootd_014_item_01.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-12-09'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '235 minutes', CURRENT_TIMESTAMP - INTERVAL '235 minutes'),
    (920067, 900004, 205, 'Carhartt WIP', '카모플라주 와이드 카고 팬츠', 'MENSWEAR', 'KR', '2', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-06-03'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '234 minutes', CURRENT_TIMESTAMP - INTERVAL '234 minutes'),
    (920068, 900004, 402, 'KAPITAL', '멀티컬러 니트 비니', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-03-24'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '233 minutes', CURRENT_TIMESTAMP - INTERVAL '233 minutes'),
    (920069, 900004, 710, 'HUMAN MADE', '그린 그래픽 에코백', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-01-08'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '232 minutes', CURRENT_TIMESTAMP - INTERVAL '232 minutes'),
    (920070, 900004, 503, 'G.H.BASS', '블랙 페니 로퍼', 'UNISEX', 'KR', '235', '/files/seed/ian/products/ootd_014_item_05.jpg', 'seed/ian/products/ootd_014_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-12-09'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '231 minutes', CURRENT_TIMESTAMP - INTERVAL '231 minutes'),
    (920071, 900005, 101, 'UNIQLO U', '화이트 오버핏 반팔 티셔츠', 'MENSWEAR', 'KR', 'XL', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-05-24'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '230 minutes', CURRENT_TIMESTAMP - INTERVAL '230 minutes'),
    (920072, 900005, 201, 'Dickies', '라임 옐로 와이드 코튼 팬츠', 'MENSWEAR', 'KR', '50', '/files/seed/ian/products/ootd_015_item_02.jpg', 'seed/ian/products/ootd_015_item_02.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-11-01'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '229 minutes', CURRENT_TIMESTAMP - INTERVAL '229 minutes'),
    (920073, 900005, 401, 'Stussy', '블루 메시지 로고 볼캡', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-05-01'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '228 minutes', CURRENT_TIMESTAMP - INTERVAL '228 minutes'),
    (920074, 900005, 606, 'Oakley', '블랙 랩 선글라스', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_015_item_04.jpg', 'seed/ian/products/ootd_015_item_04.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-06-19'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '227 minutes', CURRENT_TIMESTAMP - INTERVAL '227 minutes'),
    (920075, 900005, 503, 'Clarks', '브라운 왈라비 슈즈', 'MENSWEAR', 'KR', '260', '/files/seed/ian/products/ootd_015_item_05.jpg', 'seed/ian/products/ootd_015_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-06-13'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '226 minutes', CURRENT_TIMESTAMP - INTERVAL '226 minutes'),
    (920076, 900006, 109, 'COS', '라임 옐로 브이넥 니트', 'WOMENSWEAR', 'KR', '1', '/files/seed/ian/products/ootd_016_item_01.jpeg', 'seed/ian/products/ootd_016_item_01.jpeg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-10-08'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '225 minutes', CURRENT_TIMESTAMP - INTERVAL '225 minutes'),
    (920077, 900006, 102, 'UNIQLO U', '블랙 화이트 스트라이프 긴팔 티셔츠', 'WOMENSWEAR', 'KR', 'S', '/files/seed/ian/products/ootd_016_item_02.jpg', 'seed/ian/products/ootd_016_item_02.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-04-19'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '224 minutes', CURRENT_TIMESTAMP - INTERVAL '224 minutes'),
    (920078, 900006, 202, 'Levi''s', '미드 블루 와이드 데님 팬츠', 'WOMENSWEAR', 'KR', 'L', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-10-13'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '223 minutes', CURRENT_TIMESTAMP - INTERVAL '223 minutes'),
    (920079, 900006, 501, 'Converse', '브라운 척 70 스니커즈', 'UNISEX', 'KR', '235', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-03-13'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '222 minutes', CURRENT_TIMESTAMP - INTERVAL '222 minutes'),
    (920080, 900006, 606, 'Gentle Monster', '블랙 스퀘어 안경', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-09-06'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '221 minutes', CURRENT_TIMESTAMP - INTERVAL '221 minutes'),
    (920081, 900007, 308, 'Theory', '그레이 테일러드 블레이저', 'WOMENSWEAR', 'KR', '85', '/files/seed/ian/products/ootd_017_item_01.jpg', 'seed/ian/products/ootd_017_item_01.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-01-12'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '220 minutes', CURRENT_TIMESTAMP - INTERVAL '220 minutes'),
    (920082, 900007, 109, 'COS', '라임 브이넥 니트', 'WOMENSWEAR', 'KR', '38', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-10-19'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '219 minutes', CURRENT_TIMESTAMP - INTERVAL '219 minutes'),
    (920083, 900007, 104, 'MUJI', '블랙 체크 셔츠', 'WOMENSWEAR', 'KR', 'L', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-11-10'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '218 minutes', CURRENT_TIMESTAMP - INTERVAL '218 minutes'),
    (920084, 900007, 201, 'Levi''s', '그레이 코듀로이 와이드 팬츠', 'WOMENSWEAR', 'KR', '38', '/files/seed/ian/products/ootd_017_item_04.jpg', 'seed/ian/products/ootd_017_item_04.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-03-02'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '217 minutes', CURRENT_TIMESTAMP - INTERVAL '217 minutes'),
    (920085, 900007, 401, 'adidas', '블랙 그린 로고 볼캡', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_017_item_05.jpg', 'seed/ian/products/ootd_017_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-02-03'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '216 minutes', CURRENT_TIMESTAMP - INTERVAL '216 minutes'),
    (920086, 900008, 101, 'Lemaire', '차콜 레귤러핏 반팔 티셔츠', 'MENSWEAR', 'KR', 'L', '/files/seed/ian/products/ootd_018_item_01.jpg', 'seed/ian/products/ootd_018_item_01.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-04-07'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '215 minutes', CURRENT_TIMESTAMP - INTERVAL '215 minutes'),
    (920087, 900008, 110, 'Our Legacy', '브라운 스트라이프 니트 가디건', 'MENSWEAR', 'KR', 'L', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-01-20'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '214 minutes', CURRENT_TIMESTAMP - INTERVAL '214 minutes'),
    (920088, 900008, 202, 'Levi''s', '그레이 워시 스트레이트 데님 팬츠', 'MENSWEAR', 'KR', 'L', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-12-16'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '213 minutes', CURRENT_TIMESTAMP - INTERVAL '213 minutes'),
    (920089, 900008, 506, 'Red Wing', '브라운 레더 워크 부츠', 'MENSWEAR', 'KR', '265', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-12-29'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '212 minutes', CURRENT_TIMESTAMP - INTERVAL '212 minutes'),
    (920090, 900008, 802, 'Maison Margiela', '실버 볼드 링', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_018_item_05.jpg', 'seed/ian/products/ootd_018_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-02-06'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '211 minutes', CURRENT_TIMESTAMP - INTERVAL '211 minutes'),
    (920091, 900009, 110, 'Our Legacy', '블랙 리브 집업 가디건', 'MENSWEAR', 'KR', '95', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-08-31'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '210 minutes', CURRENT_TIMESTAMP - INTERVAL '210 minutes'),
    (920092, 900009, 602, 'Polo Ralph Lauren', '블랙 울 머플러', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-05-18'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '209 minutes', CURRENT_TIMESTAMP - INTERVAL '209 minutes'),
    (920093, 900009, 202, 'Levi''s', '인디고 와이드 데님 팬츠', 'MENSWEAR', 'KR', '2XL', '/files/seed/ian/products/ootd_019_item_03.jpg', 'seed/ian/products/ootd_019_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-09-11'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '208 minutes', CURRENT_TIMESTAMP - INTERVAL '208 minutes'),
    (920094, 900009, 501, 'New Balance', '블랙 스웨이드 스니커즈', 'UNISEX', 'KR', '275', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-12-22'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '207 minutes', CURRENT_TIMESTAMP - INTERVAL '207 minutes'),
    (920095, 900009, 402, 'Stussy', '블랙 리브 비니', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-05-20'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '206 minutes', CURRENT_TIMESTAMP - INTERVAL '206 minutes'),
    (920096, 900010, 109, 'COS', '옐로 브이넥 오버핏 니트', 'WOMENSWEAR', 'KR', '95', '/files/seed/ian/products/ootd_020_item_01.jpg', 'seed/ian/products/ootd_020_item_01.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-11-27'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '205 minutes', CURRENT_TIMESTAMP - INTERVAL '205 minutes'),
    (920097, 900010, 102, 'UNIQLO U', '블랙 화이트 스트라이프 긴팔 티셔츠', 'WOMENSWEAR', 'KR', '1', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-01-07'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '204 minutes', CURRENT_TIMESTAMP - INTERVAL '204 minutes'),
    (920098, 900010, 202, 'Levi''s', '미드 블루 와이드 데님 팬츠', 'WOMENSWEAR', 'KR', 'L', '/files/seed/ian/products/ootd_020_item_03.jpg', 'seed/ian/products/ootd_020_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-10-27'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '203 minutes', CURRENT_TIMESTAMP - INTERVAL '203 minutes'),
    (920099, 900010, 501, 'Converse', '차콜 척 70 스니커즈', 'UNISEX', 'KR', '260', '/files/seed/ian/products/ootd_020_item_04.jpg', 'seed/ian/products/ootd_020_item_04.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-06-07'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '202 minutes', CURRENT_TIMESTAMP - INTERVAL '202 minutes'),
    (920100, 900010, 701, 'EASTPAK', '버건디 데일리 백팩', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_020_item_05.jpg', 'seed/ian/products/ootd_020_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-05-11'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '201 minutes', CURRENT_TIMESTAMP - INTERVAL '201 minutes'),
    (920101, 900101, 307, 'Champion', '헤더 그레이 후드 집업', 'MENSWEAR', 'KR', '44', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-08-27'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '200 minutes', CURRENT_TIMESTAMP - INTERVAL '200 minutes'),
    (920102, 900101, 101, 'UNIQLO U', '화이트 레이어드 반팔 티셔츠', 'MENSWEAR', 'KR', '3', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-06-25'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '199 minutes', CURRENT_TIMESTAMP - INTERVAL '199 minutes'),
    (920103, 900101, 206, 'COS', '블랙 와이드 트라우저', 'MENSWEAR', 'KR', 'L', '/files/seed/ian/products/ootd_021_item_03.jpeg', 'seed/ian/products/ootd_021_item_03.jpeg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-02-17'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '198 minutes', CURRENT_TIMESTAMP - INTERVAL '198 minutes'),
    (920104, 900101, 401, 'New Era', '로열 블루 볼캡', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_021_item_04.jpg', 'seed/ian/products/ootd_021_item_04.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-08-19'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '197 minutes', CURRENT_TIMESTAMP - INTERVAL '197 minutes'),
    (920105, 900101, 503, 'Dr. Martens', '블랙 스퀘어 토 로퍼', 'MENSWEAR', 'KR', '275', '/files/seed/ian/products/ootd_021_item_05.jpg', 'seed/ian/products/ootd_021_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-12-26'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '196 minutes', CURRENT_TIMESTAMP - INTERVAL '196 minutes'),
    (920106, 900102, 303, 'Levi''s', '다크 워시 크롭 데님 재킷', 'WOMENSWEAR', 'KR', 'M', '/files/seed/ian/products/ootd_022_item_01.jpg', 'seed/ian/products/ootd_022_item_01.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-05-26'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '195 minutes', CURRENT_TIMESTAMP - INTERVAL '195 minutes'),
    (920107, 900102, 102, 'UNIQLO U', '블랙 화이트 스트라이프 긴팔 티셔츠', 'WOMENSWEAR', 'KR', 'M', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-10-20'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '194 minutes', CURRENT_TIMESTAMP - INTERVAL '194 minutes'),
    (920108, 900102, 206, 'COS', '차콜 벌룬 트라우저', 'WOMENSWEAR', 'KR', 'L', '/files/seed/ian/products/ootd_022_item_03.jpg', 'seed/ian/products/ootd_022_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-07-03'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '193 minutes', CURRENT_TIMESTAMP - INTERVAL '193 minutes'),
    (920109, 900102, 505, 'Dr. Martens', '블랙 레더 앵클 부츠', 'WOMENSWEAR', 'KR', '235', '/files/seed/ian/products/ootd_022_item_04.jpg', 'seed/ian/products/ootd_022_item_04.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-06-04'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '192 minutes', CURRENT_TIMESTAMP - INTERVAL '192 minutes'),
    (920110, 900102, 809, 'Chrome Hearts', '실버 체인 목걸이', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-10-20'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '191 minutes', CURRENT_TIMESTAMP - INTERVAL '191 minutes'),
    (920111, 900103, 301, 'Alpha Industries', '세이지 그린 MA-1 블루종', 'MENSWEAR', 'KR', '40', '/files/seed/ian/products/ootd_023_item_01.jpg', 'seed/ian/products/ootd_023_item_01.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-05-18'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '190 minutes', CURRENT_TIMESTAMP - INTERVAL '190 minutes'),
    (920112, 900103, 108, 'Champion', '레드 오버핏 후디', 'MENSWEAR', 'KR', '2XL', '/files/seed/ian/products/ootd_023_item_02.jpg', 'seed/ian/products/ootd_023_item_02.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-01-20'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '189 minutes', CURRENT_TIMESTAMP - INTERVAL '189 minutes'),
    (920113, 900103, 208, 'Needles', '네이비 사이드라인 트랙 팬츠', 'MENSWEAR', 'KR', '46', '/files/seed/ian/products/ootd_023_item_03.jpg', 'seed/ian/products/ootd_023_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-12-18'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '188 minutes', CURRENT_TIMESTAMP - INTERVAL '188 minutes'),
    (920114, 900103, 403, 'New Era', '버건디 NY 버킷햇', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_023_item_04.jpg', 'seed/ian/products/ootd_023_item_04.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-05-27'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '187 minutes', CURRENT_TIMESTAMP - INTERVAL '187 minutes'),
    (920115, 900103, 507, 'THE NORTH FACE', '블랙 눕시 뮬 슈즈', 'UNISEX', 'KR', '270', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-10-12'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '186 minutes', CURRENT_TIMESTAMP - INTERVAL '186 minutes'),
    (920116, 900104, 401, 'Stussy', '그린 로고 트러커 볼캡', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-11-24'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '185 minutes', CURRENT_TIMESTAMP - INTERVAL '185 minutes'),
    (920117, 900104, 108, 'Stussy', '워시드 블루 디스트로이드 후디', 'MENSWEAR', 'KR', '44', '/files/seed/ian/products/ootd_024_item_02.jpg', 'seed/ian/products/ootd_024_item_02.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-07-23'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '184 minutes', CURRENT_TIMESTAMP - INTERVAL '184 minutes'),
    (920118, 900104, 210, 'Dickies', '차콜 와이드 쇼츠', 'MENSWEAR', 'KR', '3', '/files/seed/ian/products/ootd_024_item_03.jpg', 'seed/ian/products/ootd_024_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-06-12'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '183 minutes', CURRENT_TIMESTAMP - INTERVAL '183 minutes'),
    (920119, 900104, 503, 'G.H.BASS', '브라운 페니 로퍼', 'MENSWEAR', 'KR', '270', '/files/seed/ian/products/ootd_024_item_04.jpg', 'seed/ian/products/ootd_024_item_04.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-11-19'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '182 minutes', CURRENT_TIMESTAMP - INTERVAL '182 minutes'),
    (920120, 900104, 708, 'Polo Ralph Lauren', '브라운 위빙 더플백', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_024_item_05.jpg', 'seed/ian/products/ootd_024_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-05-25'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '181 minutes', CURRENT_TIMESTAMP - INTERVAL '181 minutes'),
    (920121, 900105, 308, 'Ralph Lauren', '브라운 체크 울 블레이저', 'MENSWEAR', 'KR', 'M', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-09-05'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '180 minutes', CURRENT_TIMESTAMP - INTERVAL '180 minutes'),
    (920122, 900105, 101, 'Hanes', '화이트 크루넥 반팔 티셔츠', 'MENSWEAR', 'KR', 'M', '/files/seed/ian/products/ootd_025_item_02.jpg', 'seed/ian/products/ootd_025_item_02.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-04-01'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '179 minutes', CURRENT_TIMESTAMP - INTERVAL '179 minutes'),
    (920123, 900105, 206, 'Theory', '다크 브라운 와이드 트라우저', 'MENSWEAR', 'KR', '46', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-09-11'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '178 minutes', CURRENT_TIMESTAMP - INTERVAL '178 minutes'),
    (920124, 900105, 503, 'G.H.BASS', '브라운 페니 로퍼', 'MENSWEAR', 'KR', '270', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-07-28'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '177 minutes', CURRENT_TIMESTAMP - INTERVAL '177 minutes'),
    (920125, 900105, 601, 'A.P.C.', '브라운 슬림 레더 벨트', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-12-12'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '176 minutes', CURRENT_TIMESTAMP - INTERVAL '176 minutes'),
    (920126, 900106, 303, 'Levi''s', '다크 인디고 트러커 데님 재킷', 'MENSWEAR', 'KR', '2XL', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-10-18'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '175 minutes', CURRENT_TIMESTAMP - INTERVAL '175 minutes'),
    (920127, 900106, 101, 'UNIQLO U', '화이트 크루넥 반팔 티셔츠', 'MENSWEAR', 'KR', '44', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-03-26'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '174 minutes', CURRENT_TIMESTAMP - INTERVAL '174 minutes'),
    (920128, 900106, 201, 'Dickies', '베이지 와이드 코튼 팬츠', 'MENSWEAR', 'KR', 'L', '/files/seed/ian/products/ootd_026_item_03.jpg', 'seed/ian/products/ootd_026_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-04-27'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '173 minutes', CURRENT_TIMESTAMP - INTERVAL '173 minutes'),
    (920129, 900106, 402, 'Stussy', '로열 블루 리브 비니', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_026_item_04.jpg', 'seed/ian/products/ootd_026_item_04.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-12-06'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '172 minutes', CURRENT_TIMESTAMP - INTERVAL '172 minutes'),
    (920130, 900106, 503, 'G.H.BASS', '버건디 레더 로퍼', 'MENSWEAR', 'KR', '265', '/files/seed/ian/products/ootd_026_item_05.jpg', 'seed/ian/products/ootd_026_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-06-23'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '171 minutes', CURRENT_TIMESTAMP - INTERVAL '171 minutes'),
    (920131, 900107, 304, 'Patagonia', '오렌지 나일론 윈드브레이커', 'MENSWEAR', 'KR', '2XL', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-02-15'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '170 minutes', CURRENT_TIMESTAMP - INTERVAL '170 minutes'),
    (920132, 900107, 108, 'Champion', '그레이 오버핏 후디', 'MENSWEAR', 'KR', '100', '/files/seed/ian/products/ootd_027_item_02.jpg', 'seed/ian/products/ootd_027_item_02.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-07-08'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '169 minutes', CURRENT_TIMESTAMP - INTERVAL '169 minutes'),
    (920133, 900107, 207, 'Champion', '그레이 리버스 위브 스웨트팬츠', 'MENSWEAR', 'KR', '44', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-03-18'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '168 minutes', CURRENT_TIMESTAMP - INTERVAL '168 minutes'),
    (920134, 900107, 401, 'New Era', '블랙 로고 볼캡', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-12-14'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '167 minutes', CURRENT_TIMESTAMP - INTERVAL '167 minutes'),
    (920135, 900107, 502, 'ASICS', '네이비 젤 카야노 러닝화', 'UNISEX', 'KR', '260', '/files/seed/ian/products/ootd_027_item_05.jpg', 'seed/ian/products/ootd_027_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-07-14'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '166 minutes', CURRENT_TIMESTAMP - INTERVAL '166 minutes'),
    (920136, 900108, 313, 'Carhartt WIP', '네이비 코튼 워크 재킷', 'MENSWEAR', 'KR', '2', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-07-02'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '165 minutes', CURRENT_TIMESTAMP - INTERVAL '165 minutes'),
    (920137, 900108, 101, 'Stussy', '오렌지 크루넥 반팔 티셔츠', 'MENSWEAR', 'KR', '2XL', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-10-15'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '164 minutes', CURRENT_TIMESTAMP - INTERVAL '164 minutes'),
    (920138, 900108, 201, 'Dickies', '올리브 와이드 코튼 팬츠', 'MENSWEAR', 'KR', '44', '/files/seed/ian/products/ootd_028_item_03.jpg', 'seed/ian/products/ootd_028_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-08-14'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '163 minutes', CURRENT_TIMESTAMP - INTERVAL '163 minutes'),
    (920139, 900108, 701, 'PORTER', '블랙 나일론 백팩', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-05-15'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '162 minutes', CURRENT_TIMESTAMP - INTERVAL '162 minutes'),
    (920140, 900108, 401, 'Stussy', '블랙 로고 볼캡', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_028_item_05.jpg', 'seed/ian/products/ootd_028_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-07-10'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '161 minutes', CURRENT_TIMESTAMP - INTERVAL '161 minutes'),
    (920141, 900109, 307, 'Palace', '워시드 블루 후드 집업', 'MENSWEAR', 'KR', 'M', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-03-17'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '160 minutes', CURRENT_TIMESTAMP - INTERVAL '160 minutes'),
    (920142, 900109, 202, 'Levi''s', '라이트 워시 와이드 데님 팬츠', 'MENSWEAR', 'KR', '2XL', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-12-09'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '159 minutes', CURRENT_TIMESTAMP - INTERVAL '159 minutes'),
    (920143, 900109, 401, 'Chrome Hearts', '버건디 로고 볼캡', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_029_item_03.jpg', 'seed/ian/products/ootd_029_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-06-26'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '158 minutes', CURRENT_TIMESTAMP - INTERVAL '158 minutes'),
    (920144, 900109, 606, 'Oakley', '블랙 실드 선글라스', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_029_item_04.jpg', 'seed/ian/products/ootd_029_item_04.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-03-07'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '157 minutes', CURRENT_TIMESTAMP - INTERVAL '157 minutes'),
    (920145, 900109, 502, 'New Balance', '블랙 1906 러닝화', 'UNISEX', 'KR', '255', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-08-27'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '156 minutes', CURRENT_TIMESTAMP - INTERVAL '156 minutes'),
    (920146, 900110, 301, 'Schott NYC', '네이비 플라이트 블루종', 'MENSWEAR', 'KR', '2', '/files/seed/ian/products/ootd_030_item_01.jpg', 'seed/ian/products/ootd_030_item_01.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-05-14'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '155 minutes', CURRENT_TIMESTAMP - INTERVAL '155 minutes'),
    (920147, 900110, 109, 'Polo Ralph Lauren', '멀티컬러 페어아일 니트', 'MENSWEAR', 'KR', 'XL', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-05-22'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '154 minutes', CURRENT_TIMESTAMP - INTERVAL '154 minutes'),
    (920148, 900110, 206, 'Theory', '라이트 그레이 와이드 트라우저', 'MENSWEAR', 'KR', '2XL', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-08-28'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '153 minutes', CURRENT_TIMESTAMP - INTERVAL '153 minutes'),
    (920149, 900110, 503, 'G.H.BASS', '브라운 태슬 로퍼', 'MENSWEAR', 'KR', '275', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-05-13'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '152 minutes', CURRENT_TIMESTAMP - INTERVAL '152 minutes'),
    (920150, 900110, 401, 'New Era', '블랙 NY 로고 볼캡', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_030_item_05.jpg', 'seed/ian/products/ootd_030_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-11-27'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '151 minutes', CURRENT_TIMESTAMP - INTERVAL '151 minutes'),
    (920151, 900001, 301, 'Schott NYC', '브라운 플라이트 블루종', 'MENSWEAR', 'KR', '42', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-09-16'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '150 minutes', CURRENT_TIMESTAMP - INTERVAL '150 minutes'),
    (920152, 900001, 108, 'Champion', '차콜 레이어드 후디', 'MENSWEAR', 'KR', '46', '/files/seed/ian/products/ootd_031_item_02.jpg', 'seed/ian/products/ootd_031_item_02.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-05-05'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '149 minutes', CURRENT_TIMESTAMP - INTERVAL '149 minutes'),
    (920153, 900001, 202, 'Levi''s', '미드 블루 와이드 데님 팬츠', 'MENSWEAR', 'KR', '50', '/files/seed/ian/products/ootd_031_item_03.jpg', 'seed/ian/products/ootd_031_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-05-01'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '148 minutes', CURRENT_TIMESTAMP - INTERVAL '148 minutes'),
    (920154, 900001, 402, 'Lacoste', '차콜 로고 비니', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_031_item_04.jpg', 'seed/ian/products/ootd_031_item_04.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-02-26'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '147 minutes', CURRENT_TIMESTAMP - INTERVAL '147 minutes'),
    (920155, 900001, 505, 'Tony Lama', '브라운 웨스턴 부츠', 'MENSWEAR', 'KR', '270', '/files/seed/ian/products/ootd_031_item_05.jpg', 'seed/ian/products/ootd_031_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-03-21'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '146 minutes', CURRENT_TIMESTAMP - INTERVAL '146 minutes'),
    (920156, 900002, 109, 'COS', '브라이트 블루 크루넥 니트', 'MENSWEAR', 'KR', '100', '/files/seed/ian/products/ootd_032_item_01.jpg', 'seed/ian/products/ootd_032_item_01.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-03-09'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '145 minutes', CURRENT_TIMESTAMP - INTERVAL '145 minutes'),
    (920157, 900002, 205, 'Carhartt WIP', '오프화이트 와이드 카고 팬츠', 'MENSWEAR', 'KR', 'L', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-10-14'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '144 minutes', CURRENT_TIMESTAMP - INTERVAL '144 minutes'),
    (920158, 900002, 401, 'New Era', '네이비 NY 볼캡', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-07-08'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '143 minutes', CURRENT_TIMESTAMP - INTERVAL '143 minutes'),
    (920159, 900002, 606, 'Gentle Monster', '블랙 라운드 안경', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-10-12'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '142 minutes', CURRENT_TIMESTAMP - INTERVAL '142 minutes'),
    (920160, 900002, 501, 'New Balance', '그레이 990 스니커즈', 'UNISEX', 'KR', '270', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-08-13'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '141 minutes', CURRENT_TIMESTAMP - INTERVAL '141 minutes'),
    (920161, 900003, 304, 'Patagonia', '블루 토렌쉘 윈드브레이커', 'WOMENSWEAR', 'KR', 'L', '/files/seed/ian/products/ootd_033_item_01.jpg', 'seed/ian/products/ootd_033_item_01.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-12-09'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '140 minutes', CURRENT_TIMESTAMP - INTERVAL '140 minutes'),
    (920162, 900003, 202, 'Levi''s', '블랙 워시 와이드 데님 팬츠', 'WOMENSWEAR', 'KR', 'XS', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-11-02'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '139 minutes', CURRENT_TIMESTAMP - INTERVAL '139 minutes'),
    (920163, 900003, 505, 'Dr. Martens', '블랙 레더 부츠', 'WOMENSWEAR', 'KR', '240', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-06-07'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '138 minutes', CURRENT_TIMESTAMP - INTERVAL '138 minutes'),
    (920164, 900003, 601, 'A.P.C.', '블랙 슬림 레더 벨트', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-08-01'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '137 minutes', CURRENT_TIMESTAMP - INTERVAL '137 minutes'),
    (920165, 900003, 102, 'Patagonia', '블랙 베이스레이어 긴팔 티셔츠', 'WOMENSWEAR', 'KR', 'XS', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-12-14'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '136 minutes', CURRENT_TIMESTAMP - INTERVAL '136 minutes'),
    (920166, 900004, 313, 'Carhartt WIP', '브라운 디트로이트 워크 재킷', 'MENSWEAR', 'KR', '2', '/files/seed/ian/products/ootd_034_item_01.jpg', 'seed/ian/products/ootd_034_item_01.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-07-15'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '135 minutes', CURRENT_TIMESTAMP - INTERVAL '135 minutes'),
    (920167, 900004, 108, 'Champion', '그레이 오버핏 후디', 'MENSWEAR', 'KR', 'L', '/files/seed/ian/products/ootd_034_item_02.jpg', 'seed/ian/products/ootd_034_item_02.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-07-14'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '134 minutes', CURRENT_TIMESTAMP - INTERVAL '134 minutes'),
    (920168, 900004, 201, 'Carhartt WIP', '블랙 더블니 워크 팬츠', 'MENSWEAR', 'KR', '4', '/files/seed/ian/products/ootd_034_item_03.jpg', 'seed/ian/products/ootd_034_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-02-23'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '133 minutes', CURRENT_TIMESTAMP - INTERVAL '133 minutes'),
    (920169, 900004, 402, 'Stussy', '블랙 로고 비니', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_034_item_04.jpg', 'seed/ian/products/ootd_034_item_04.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-10-22'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '132 minutes', CURRENT_TIMESTAMP - INTERVAL '132 minutes'),
    (920170, 900004, 501, 'Converse', '블랙 척 70 스니커즈', 'UNISEX', 'KR', '255', '/files/seed/ian/products/ootd_034_item_05.jpg', 'seed/ian/products/ootd_034_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-06-22'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '131 minutes', CURRENT_TIMESTAMP - INTERVAL '131 minutes'),
    (920171, 900005, 309, 'Patagonia', '퍼플 레트로 플리스 재킷', 'WOMENSWEAR', 'KR', 'S', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-05-14'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '130 minutes', CURRENT_TIMESTAMP - INTERVAL '130 minutes'),
    (920172, 900005, 205, 'THE NORTH FACE', '네이비 트레킹 팬츠', 'WOMENSWEAR', 'KR', '1', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-08-15'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '129 minutes', CURRENT_TIMESTAMP - INTERVAL '129 minutes'),
    (920173, 900005, 502, 'Salomon', '블랙 그린 XT-6 스니커즈', 'UNISEX', 'KR', '250', '/files/seed/ian/products/ootd_035_item_03.jpg', 'seed/ian/products/ootd_035_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-12-31'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '128 minutes', CURRENT_TIMESTAMP - INTERVAL '128 minutes'),
    (920174, 900005, 403, 'THE NORTH FACE', '베이지 와이드 브림 햇', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-01-28'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '127 minutes', CURRENT_TIMESTAMP - INTERVAL '127 minutes'),
    (920175, 900005, 606, 'Oakley', '브라운 스포츠 선글라스', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_035_item_05.jpg', 'seed/ian/products/ootd_035_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-07-31'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '126 minutes', CURRENT_TIMESTAMP - INTERVAL '126 minutes'),
    (920176, 900006, 304, 'Ralph Lauren', '올리브 후디드 아노락 재킷', 'WOMENSWEAR', 'KR', '38', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-12-05'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '125 minutes', CURRENT_TIMESTAMP - INTERVAL '125 minutes'),
    (920177, 900006, 108, 'Champion', '그레이 레이어드 후디', 'WOMENSWEAR', 'KR', 'L', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-10-04'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '124 minutes', CURRENT_TIMESTAMP - INTERVAL '124 minutes'),
    (920178, 900006, 202, 'Carhartt WIP', '인디고 더블니 데님 팬츠', 'WOMENSWEAR', 'KR', 'L', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-08-05'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '123 minutes', CURRENT_TIMESTAMP - INTERVAL '123 minutes'),
    (920179, 900006, 506, 'Timberland', '위트 6인치 워커', 'UNISEX', 'KR', '250', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-10-22'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '122 minutes', CURRENT_TIMESTAMP - INTERVAL '122 minutes'),
    (920180, 900006, 701, 'A.P.C.', '블랙 데일리 백팩', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_036_item_05.jpg', 'seed/ian/products/ootd_036_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-11-17'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '121 minutes', CURRENT_TIMESTAMP - INTERVAL '121 minutes'),
    (920181, 900007, 301, 'Alpha Industries', '올리브 MA-1 블루종', 'MENSWEAR', 'KR', '95', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-09-04'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '120 minutes', CURRENT_TIMESTAMP - INTERVAL '120 minutes'),
    (920182, 900007, 101, 'UNIQLO U', '네이비 스트라이프 반팔 티셔츠', 'MENSWEAR', 'KR', 'M', '/files/seed/ian/products/ootd_037_item_02.jpg', 'seed/ian/products/ootd_037_item_02.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-08-09'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '119 minutes', CURRENT_TIMESTAMP - INTERVAL '119 minutes'),
    (920183, 900007, 202, 'Levi''s', '다크 인디고 와이드 데님 팬츠', 'MENSWEAR', 'KR', 'XL', '/files/seed/ian/products/ootd_037_item_03.jpg', 'seed/ian/products/ootd_037_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-08-24'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '118 minutes', CURRENT_TIMESTAMP - INTERVAL '118 minutes'),
    (920184, 900007, 503, 'G.H.BASS', '블랙 레더 로퍼', 'MENSWEAR', 'KR', '275', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-02-06'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '117 minutes', CURRENT_TIMESTAMP - INTERVAL '117 minutes'),
    (920185, 900007, 601, 'A.P.C.', '블랙 슬림 레더 벨트', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-09-22'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '116 minutes', CURRENT_TIMESTAMP - INTERVAL '116 minutes'),
    (920186, 900008, 313, 'Aimé Leon Dore', '빈티지 블루 초어 재킷', 'MENSWEAR', 'KR', '95', '/files/seed/ian/products/ootd_038_item_01.jpg', 'seed/ian/products/ootd_038_item_01.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-02-16'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '115 minutes', CURRENT_TIMESTAMP - INTERVAL '115 minutes'),
    (920187, 900008, 101, 'UNIQLO U', '화이트 크루넥 반팔 티셔츠', 'MENSWEAR', 'KR', '2XL', '/files/seed/ian/products/ootd_038_item_02.jpg', 'seed/ian/products/ootd_038_item_02.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-07-18'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '114 minutes', CURRENT_TIMESTAMP - INTERVAL '114 minutes'),
    (920188, 900008, 202, 'Levi''s', '미드 블루 와이드 데님 팬츠', 'MENSWEAR', 'KR', 'L', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-08-14'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '113 minutes', CURRENT_TIMESTAMP - INTERVAL '113 minutes'),
    (920189, 900008, 503, 'G.H.BASS', '블랙 페니 로퍼', 'MENSWEAR', 'KR', '285', '/files/seed/ian/products/ootd_038_item_04.jpg', 'seed/ian/products/ootd_038_item_04.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-11-07'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '112 minutes', CURRENT_TIMESTAMP - INTERVAL '112 minutes'),
    (920190, 900008, 606, 'Ray-Ban', '브라운 스퀘어 선글라스', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-08-31'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '111 minutes', CURRENT_TIMESTAMP - INTERVAL '111 minutes'),
    (920191, 900009, 305, 'adidas', '블루 사이드라인 트랙 재킷', 'MENSWEAR', 'KR', '2', '/files/seed/ian/products/ootd_039_item_01.jpg', 'seed/ian/products/ootd_039_item_01.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-06-23'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '110 minutes', CURRENT_TIMESTAMP - INTERVAL '110 minutes'),
    (920192, 900009, 101, 'UNIQLO U', '블랙 크루넥 반팔 티셔츠', 'MENSWEAR', 'KR', '3', '/files/seed/ian/products/ootd_039_item_02.jpg', 'seed/ian/products/ootd_039_item_02.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-09-02'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '109 minutes', CURRENT_TIMESTAMP - INTERVAL '109 minutes'),
    (920193, 900009, 201, 'Carhartt WIP', '베이지 와이드 코튼 팬츠', 'MENSWEAR', 'KR', '44', '/files/seed/ian/products/ootd_039_item_03.jpg', 'seed/ian/products/ootd_039_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-03-29'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '108 minutes', CURRENT_TIMESTAMP - INTERVAL '108 minutes'),
    (920194, 900009, 405, 'KANGOL', '멀티 스트라이프 헌팅캡', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_039_item_04.jpeg', 'seed/ian/products/ootd_039_item_04.jpeg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-01-24'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '107 minutes', CURRENT_TIMESTAMP - INTERVAL '107 minutes'),
    (920195, 900009, 505, 'Dr. Martens', '블랙 스퀘어 토 부츠', 'MENSWEAR', 'KR', '270', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-10-16'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '106 minutes', CURRENT_TIMESTAMP - INTERVAL '106 minutes'),
    (920196, 900010, 309, 'Patagonia', '그레이 레트로 파일 플리스', 'MENSWEAR', 'KR', '110', '/files/seed/ian/products/ootd_040_item_01.jpg', 'seed/ian/products/ootd_040_item_01.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-01-30'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '105 minutes', CURRENT_TIMESTAMP - INTERVAL '105 minutes'),
    (920197, 900010, 205, 'Carhartt WIP', '올리브 와이드 카고 팬츠', 'MENSWEAR', 'KR', '3', '/files/seed/ian/products/ootd_040_item_02.jpg', 'seed/ian/products/ootd_040_item_02.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-01-20'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '104 minutes', CURRENT_TIMESTAMP - INTERVAL '104 minutes'),
    (920198, 900010, 709, 'PORTER', '브라운 나일론 슬링백', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_040_item_03.jpg', 'seed/ian/products/ootd_040_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-02-22'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '103 minutes', CURRENT_TIMESTAMP - INTERVAL '103 minutes'),
    (920199, 900010, 501, 'New Balance', '그레이 990 스니커즈', 'UNISEX', 'KR', '280', '/files/seed/ian/products/ootd_040_item_04.jpg', 'seed/ian/products/ootd_040_item_04.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-02-09'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '102 minutes', CURRENT_TIMESTAMP - INTERVAL '102 minutes'),
    (920200, 900010, 402, 'Stussy', '차콜 리브 비니', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_040_item_05.jpg', 'seed/ian/products/ootd_040_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-10-18'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '101 minutes', CURRENT_TIMESTAMP - INTERVAL '101 minutes'),
    (920201, 900101, 211, 'Dickies', '인디고 데님 오버올', 'MENSWEAR', 'KR', '48', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-10-31'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '100 minutes', CURRENT_TIMESTAMP - INTERVAL '100 minutes'),
    (920202, 900101, 109, 'Polo Ralph Lauren', '그린 네이비 체크 니트', 'MENSWEAR', 'KR', '40', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-04-26'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '99 minutes', CURRENT_TIMESTAMP - INTERVAL '99 minutes'),
    (920203, 900101, 506, 'Timberland', '위트 6인치 워커', 'UNISEX', 'KR', '265', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-08-03'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '98 minutes', CURRENT_TIMESTAMP - INTERVAL '98 minutes'),
    (920204, 900101, 401, 'Stussy', '블랙 베이스볼 캡', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-10-14'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '97 minutes', CURRENT_TIMESTAMP - INTERVAL '97 minutes'),
    (920205, 900101, 102, 'Hanes', '블랙 크루넥 긴팔 티셔츠', 'MENSWEAR', 'KR', '42', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-08-01'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '96 minutes', CURRENT_TIMESTAMP - INTERVAL '96 minutes'),
    (920206, 900102, 309, 'Patagonia', '핑크 신칠라 스냅 플리스', 'MENSWEAR', 'KR', '100', '/files/seed/ian/products/ootd_042_item_01.jpg', 'seed/ian/products/ootd_042_item_01.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-08-15'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '95 minutes', CURRENT_TIMESTAMP - INTERVAL '95 minutes'),
    (920207, 900102, 206, 'Theory', '차콜 와이드 트라우저', 'MENSWEAR', 'KR', 'L', '/files/seed/ian/products/ootd_042_item_02.jpg', 'seed/ian/products/ootd_042_item_02.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-10-19'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '94 minutes', CURRENT_TIMESTAMP - INTERVAL '94 minutes'),
    (920208, 900102, 401, 'Polo Ralph Lauren', '핑크 코튼 볼캡', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_042_item_03.jpg', 'seed/ian/products/ootd_042_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-11-29'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '93 minutes', CURRENT_TIMESTAMP - INTERVAL '93 minutes'),
    (920209, 900102, 503, 'G.H.BASS', '블랙 페니 로퍼', 'MENSWEAR', 'KR', '280', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-06-03'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '92 minutes', CURRENT_TIMESTAMP - INTERVAL '92 minutes'),
    (920210, 900102, 606, 'Gentle Monster', '실버 라운드 안경', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-10-20'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '91 minutes', CURRENT_TIMESTAMP - INTERVAL '91 minutes'),
    (920211, 900103, 313, 'Our Legacy', '올리브 크롭 밀리터리 재킷', 'MENSWEAR', 'KR', 'XL', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-05-17'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '90 minutes', CURRENT_TIMESTAMP - INTERVAL '90 minutes'),
    (920212, 900103, 106, 'Polo Ralph Lauren', '화이트 피케 폴로 셔츠', 'MENSWEAR', 'KR', '3', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-03-25'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '89 minutes', CURRENT_TIMESTAMP - INTERVAL '89 minutes'),
    (920213, 900103, 202, 'Levi''s', '인디고 롤업 데님 팬츠', 'MENSWEAR', 'KR', '48', '/files/seed/ian/products/ootd_043_item_03.jpeg', 'seed/ian/products/ootd_043_item_03.jpeg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-11-17'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '88 minutes', CURRENT_TIMESTAMP - INTERVAL '88 minutes'),
    (920214, 900103, 601, 'A.P.C.', '레오파드 레더 벨트', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-01-11'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '87 minutes', CURRENT_TIMESTAMP - INTERVAL '87 minutes'),
    (920215, 900103, 505, 'Maison Margiela', '블랙 스퀘어 토 부츠', 'MENSWEAR', 'KR', '275', '/files/seed/ian/products/ootd_043_item_05.jpg', 'seed/ian/products/ootd_043_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-08-04'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '86 minutes', CURRENT_TIMESTAMP - INTERVAL '86 minutes'),
    (920216, 900104, 102, 'Aimé Leon Dore', '블루 와플 긴팔 티셔츠', 'MENSWEAR', 'KR', 'M', '/files/seed/ian/products/ootd_044_item_01.jpg', 'seed/ian/products/ootd_044_item_01.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-08-31'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '85 minutes', CURRENT_TIMESTAMP - INTERVAL '85 minutes'),
    (920217, 900104, 206, 'COS', '라이트 그레이 와이드 트라우저', 'MENSWEAR', 'KR', '50', '/files/seed/ian/products/ootd_044_item_02.jpg', 'seed/ian/products/ootd_044_item_02.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-09-22'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '84 minutes', CURRENT_TIMESTAMP - INTERVAL '84 minutes'),
    (920218, 900104, 405, 'KANGOL', '블루 스트라이프 헌팅캡', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_044_item_03.jpg', 'seed/ian/products/ootd_044_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-01-09'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '83 minutes', CURRENT_TIMESTAMP - INTERVAL '83 minutes'),
    (920219, 900104, 606, 'Gentle Monster', '블랙 볼드 프레임 안경', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_044_item_04.jpg', 'seed/ian/products/ootd_044_item_04.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-11-13'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '82 minutes', CURRENT_TIMESTAMP - INTERVAL '82 minutes'),
    (920220, 900104, 809, 'Chrome Hearts', '펄 레이어드 체인 목걸이', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-04-18'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '81 minutes', CURRENT_TIMESTAMP - INTERVAL '81 minutes'),
    (920221, 900105, 101, 'Hanes', '네이비 포켓 반팔 티셔츠', 'MENSWEAR', 'KR', 'M', '/files/seed/ian/products/ootd_045_item_01.jpg', 'seed/ian/products/ootd_045_item_01.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-02-10'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '80 minutes', CURRENT_TIMESTAMP - INTERVAL '80 minutes'),
    (920222, 900105, 102, 'UNIQLO U', '화이트 와플 긴팔 티셔츠', 'MENSWEAR', 'KR', 'XL', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-04-28'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '79 minutes', CURRENT_TIMESTAMP - INTERVAL '79 minutes'),
    (920223, 900105, 202, 'Levi''s', '라이트 블루 스트레이트 데님 팬츠', 'MENSWEAR', 'KR', '50', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-05-12'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '78 minutes', CURRENT_TIMESTAMP - INTERVAL '78 minutes'),
    (920224, 900105, 502, 'New Balance', '화이트 530 러닝화', 'UNISEX', 'KR', '260', '/files/seed/ian/products/ootd_045_item_04.jpg', 'seed/ian/products/ootd_045_item_04.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-10-30'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '77 minutes', CURRENT_TIMESTAMP - INTERVAL '77 minutes'),
    (920225, 900105, 401, 'Polo Ralph Lauren', '베이지 코튼 볼캡', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_045_item_05.jpg', 'seed/ian/products/ootd_045_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-12-28'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '76 minutes', CURRENT_TIMESTAMP - INTERVAL '76 minutes'),
    (920226, 900106, 109, 'Polo Ralph Lauren', '브라운 페어아일 니트 베스트', 'MENSWEAR', 'KR', '2', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-09-18'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '75 minutes', CURRENT_TIMESTAMP - INTERVAL '75 minutes'),
    (920227, 900106, 104, 'Ralph Lauren', '화이트 옥스포드 셔츠', 'MENSWEAR', 'KR', '95', '/files/seed/ian/products/ootd_046_item_02.jpg', 'seed/ian/products/ootd_046_item_02.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-02-18'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '74 minutes', CURRENT_TIMESTAMP - INTERVAL '74 minutes'),
    (920228, 900106, 603, 'Polo Ralph Lauren', '버건디 니트 넥타이', 'MENSWEAR', 'KR', 'One size', '/files/seed/ian/products/ootd_046_item_03.jpg', 'seed/ian/products/ootd_046_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-03-19'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '73 minutes', CURRENT_TIMESTAMP - INTERVAL '73 minutes'),
    (920229, 900106, 206, 'RRL', '브라운 플리츠 울 트라우저', 'MENSWEAR', 'KR', '44', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-09-28'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '72 minutes', CURRENT_TIMESTAMP - INTERVAL '72 minutes'),
    (920230, 900106, 601, 'A.P.C.', '브라운 와이드 레더 벨트', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-03-23'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '71 minutes', CURRENT_TIMESTAMP - INTERVAL '71 minutes'),
    (920231, 900107, 110, 'RRL', '브라운 숄 칼라 니트 가디건', 'MENSWEAR', 'KR', '46', '/files/seed/ian/products/ootd_047_item_01.jpg', 'seed/ian/products/ootd_047_item_01.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-04-30'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '70 minutes', CURRENT_TIMESTAMP - INTERVAL '70 minutes'),
    (920232, 900107, 108, 'Champion', '베이지 레이어드 후디', 'MENSWEAR', 'KR', '105', '/files/seed/ian/products/ootd_047_item_02.jpg', 'seed/ian/products/ootd_047_item_02.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-01-26'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '69 minutes', CURRENT_TIMESTAMP - INTERVAL '69 minutes'),
    (920233, 900107, 205, 'Carhartt WIP', '라이트 블루 카고 데님 팬츠', 'MENSWEAR', 'KR', 'XL', '/files/seed/ian/products/ootd_047_item_03.jpg', 'seed/ian/products/ootd_047_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-12-14'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '68 minutes', CURRENT_TIMESTAMP - INTERVAL '68 minutes'),
    (920234, 900107, 503, 'Clarks', '버건디 왈라비 슈즈', 'MENSWEAR', 'KR', '275', '/files/seed/ian/products/ootd_047_item_04.jpg', 'seed/ian/products/ootd_047_item_04.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-09-05'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '67 minutes', CURRENT_TIMESTAMP - INTERVAL '67 minutes'),
    (920235, 900107, 802, 'Maison Margiela', '실버 심플 링', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-09-20'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '66 minutes', CURRENT_TIMESTAMP - INTERVAL '66 minutes'),
    (920236, 900108, 303, 'Levi''s', '라이트 워시 트러커 데님 재킷', 'MENSWEAR', 'KR', '46', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-11-09'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '65 minutes', CURRENT_TIMESTAMP - INTERVAL '65 minutes'),
    (920237, 900108, 602, 'Polo Ralph Lauren', '버건디 체크 울 머플러', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_048_item_02.jpg', 'seed/ian/products/ootd_048_item_02.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-04-11'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '64 minutes', CURRENT_TIMESTAMP - INTERVAL '64 minutes'),
    (920238, 900108, 206, 'Theory', '차콜 와이드 트라우저', 'MENSWEAR', 'KR', 'L', '/files/seed/ian/products/ootd_048_item_03.jpg', 'seed/ian/products/ootd_048_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-10-06'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '63 minutes', CURRENT_TIMESTAMP - INTERVAL '63 minutes'),
    (920239, 900108, 505, 'Blundstone', '탄 레더 첼시 부츠', 'MENSWEAR', 'KR', '275', '/files/seed/ian/products/ootd_048_item_04.jpg', 'seed/ian/products/ootd_048_item_04.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-08-13'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '62 minutes', CURRENT_TIMESTAMP - INTERVAL '62 minutes'),
    (920240, 900108, 401, 'Polo Sport', '네이비 로고 캠프캡', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-03-30'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '61 minutes', CURRENT_TIMESTAMP - INTERVAL '61 minutes'),
    (920241, 900109, 101, 'Hanes', '차콜 워시 반팔 티셔츠', 'MENSWEAR', 'KR', '3', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-02-15'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '60 minutes', CURRENT_TIMESTAMP - INTERVAL '60 minutes'),
    (920242, 900109, 202, 'Carhartt WIP', '블루 히코리 스트라이프 데님 팬츠', 'MENSWEAR', 'KR', '46', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-11-01'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '59 minutes', CURRENT_TIMESTAMP - INTERVAL '59 minutes'),
    (920243, 900109, 506, 'Red Wing', '옥스블러드 모크토 워크 부츠', 'MENSWEAR', 'KR', '270', '/files/seed/ian/products/ootd_049_item_03.jpg', 'seed/ian/products/ootd_049_item_03.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-08-18'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '58 minutes', CURRENT_TIMESTAMP - INTERVAL '58 minutes'),
    (920244, 900109, 602, 'KAPITAL', '블루 플라워 반다나', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2025-11-22'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '57 minutes', CURRENT_TIMESTAMP - INTERVAL '57 minutes'),
    (920245, 900109, 809, 'Chrome Hearts', '실버 체인 목걸이', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_049_item_05.jpg', 'seed/ian/products/ootd_049_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-05-11'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '56 minutes', CURRENT_TIMESTAMP - INTERVAL '56 minutes'),
    (920246, 900110, 101, 'UNIQLO U', '화이트 레귤러핏 반팔 티셔츠', 'MENSWEAR', 'KR', '100', '/files/seed/ian/products/ootd_050_item_01.jpg', 'seed/ian/products/ootd_050_item_01.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-06-21'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '55 minutes', CURRENT_TIMESTAMP - INTERVAL '55 minutes'),
    (920247, 900110, 202, 'Levi''s', '미드 블루 501 스트레이트 데님 팬츠', 'MENSWEAR', 'KR', '44', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-01-30'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '54 minutes', CURRENT_TIMESTAMP - INTERVAL '54 minutes'),
    (920248, 900110, 601, 'A.P.C.', '브라운 웨스턴 레더 벨트', 'UNISEX', 'KR', 'One size', NULL, NULL, 'OWNED', 1, CURRENT_DATE - 1, '2026-05-03'::date, 0, FALSE, CURRENT_TIMESTAMP - INTERVAL '53 minutes', CURRENT_TIMESTAMP - INTERVAL '53 minutes'),
    (920249, 900110, 506, 'Red Wing', '블랙 레더 워크 부츠', 'MENSWEAR', 'KR', '285', '/files/seed/ian/products/ootd_050_item_04.jpg', 'seed/ian/products/ootd_050_item_04.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2025-09-18'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '52 minutes', CURRENT_TIMESTAMP - INTERVAL '52 minutes'),
    (920250, 900110, 809, 'Chrome Hearts', '실버 월렛 체인', 'UNISEX', 'KR', 'One size', '/files/seed/ian/products/ootd_050_item_05.jpg', 'seed/ian/products/ootd_050_item_05.jpg', 'ON_SALE', 1, CURRENT_DATE - 1, '2026-04-01'::date, 0, TRUE, CURRENT_TIMESTAMP - INTERVAL '51 minutes', CURRENT_TIMESTAMP - INTERVAL '51 minutes');

INSERT INTO item_histories (id, item_id, owner_user_id, trade_id, acquisition_type, end_reason, started_at, ended_at, created_at)
VALUES
    (934001, 920001, 900101, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-01-18'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934002, 920002, 900101, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-09-24'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934003, 920003, 900101, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-04-14'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934004, 920004, 900101, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-12-22'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934005, 920005, 900101, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-09-20'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934006, 920006, 900102, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-04'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934007, 920007, 900102, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-04-02'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934008, 920008, 900102, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-31'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934009, 920009, 900102, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-08'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934010, 920010, 900102, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-12-11'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934011, 920011, 900103, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-04'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934012, 920012, 900103, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-11-14'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934013, 920013, 900103, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-13'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934014, 920014, 900103, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-17'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934015, 920015, 900103, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-11-20'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934016, 920016, 900104, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-08-02'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934017, 920017, 900104, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-01-08'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934018, 920018, 900104, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-11-23'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934019, 920019, 900104, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-02-24'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934020, 920020, 900104, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-06-15'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934021, 920021, 900105, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-09-25'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934022, 920022, 900105, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-03-29'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934023, 920023, 900105, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-11-20'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934024, 920024, 900105, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-01-01'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934025, 920025, 900105, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-03-01'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934026, 920026, 900106, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-13'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934027, 920027, 900106, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-04-16'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934028, 920028, 900106, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-03-03'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934029, 920029, 900106, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-06-23'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934030, 920030, 900106, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-07-26'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934031, 920031, 900107, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-06-10'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934032, 920032, 900107, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-01-28'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934033, 920033, 900107, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-01-05'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934034, 920034, 900107, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-01'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934035, 920035, 900107, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-31'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934036, 920036, 900108, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-14'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934037, 920037, 900108, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-12-05'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934038, 920038, 900108, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-11-06'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934039, 920039, 900108, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-03-25'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934040, 920040, 900108, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-09'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934041, 920041, 900109, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-11-15'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934042, 920042, 900109, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-11-11'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934043, 920043, 900109, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-07-12'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934044, 920044, 900109, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-12-06'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934045, 920045, 900109, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-26'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934046, 920046, 900110, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-07-19'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934047, 920047, 900110, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-01-18'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934048, 920048, 900110, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-02-17'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934049, 920049, 900110, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-01-22'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934050, 920050, 900110, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-01-09'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934051, 920051, 900001, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-04-11'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934052, 920052, 900001, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-04-22'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934053, 920053, 900001, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-12'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934054, 920054, 900001, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-21'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934055, 920055, 900001, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-07-27'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934056, 920056, 900002, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-07-17'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934057, 920057, 900002, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-09-16'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934058, 920058, 900002, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-12-05'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934059, 920059, 900002, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-08-21'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934060, 920060, 900002, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-28'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934061, 920061, 900003, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-09-26'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934062, 920062, 900003, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-11-29'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934063, 920063, 900003, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-03-06'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934064, 920064, 900003, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-03-20'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934065, 920065, 900003, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-12-15'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934066, 920066, 900004, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-12-09'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934067, 920067, 900004, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-06-03'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934068, 920068, 900004, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-03-24'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934069, 920069, 900004, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-01-08'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934070, 920070, 900004, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-12-09'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934071, 920071, 900005, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-24'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934072, 920072, 900005, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-11-01'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934073, 920073, 900005, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-01'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934074, 920074, 900005, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-06-19'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934075, 920075, 900005, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-06-13'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934076, 920076, 900006, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-08'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934077, 920077, 900006, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-04-19'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934078, 920078, 900006, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-13'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934079, 920079, 900006, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-03-13'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934080, 920080, 900006, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-09-06'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934081, 920081, 900007, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-01-12'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934082, 920082, 900007, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-19'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934083, 920083, 900007, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-11-10'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934084, 920084, 900007, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-03-02'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934085, 920085, 900007, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-02-03'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934086, 920086, 900008, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-04-07'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934087, 920087, 900008, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-01-20'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934088, 920088, 900008, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-12-16'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934089, 920089, 900008, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-12-29'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934090, 920090, 900008, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-02-06'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934091, 920091, 900009, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-08-31'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934092, 920092, 900009, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-18'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934093, 920093, 900009, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-09-11'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934094, 920094, 900009, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-12-22'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934095, 920095, 900009, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-20'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934096, 920096, 900010, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-11-27'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934097, 920097, 900010, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-01-07'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934098, 920098, 900010, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-27'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934099, 920099, 900010, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-06-07'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934100, 920100, 900010, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-11'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934101, 920101, 900101, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-08-27'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934102, 920102, 900101, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-06-25'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934103, 920103, 900101, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-02-17'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934104, 920104, 900101, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-08-19'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934105, 920105, 900101, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-12-26'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934106, 920106, 900102, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-26'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934107, 920107, 900102, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-20'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934108, 920108, 900102, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-07-03'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934109, 920109, 900102, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-06-04'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934110, 920110, 900102, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-20'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934111, 920111, 900103, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-18'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934112, 920112, 900103, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-01-20'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934113, 920113, 900103, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-12-18'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934114, 920114, 900103, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-27'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934115, 920115, 900103, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-12'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934116, 920116, 900104, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-11-24'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934117, 920117, 900104, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-07-23'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934118, 920118, 900104, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-06-12'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934119, 920119, 900104, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-11-19'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934120, 920120, 900104, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-25'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934121, 920121, 900105, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-09-05'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934122, 920122, 900105, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-04-01'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934123, 920123, 900105, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-09-11'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934124, 920124, 900105, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-07-28'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934125, 920125, 900105, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-12-12'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934126, 920126, 900106, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-18'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934127, 920127, 900106, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-03-26'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934128, 920128, 900106, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-04-27'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934129, 920129, 900106, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-12-06'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934130, 920130, 900106, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-06-23'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934131, 920131, 900107, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-02-15'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934132, 920132, 900107, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-07-08'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934133, 920133, 900107, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-03-18'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934134, 920134, 900107, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-12-14'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934135, 920135, 900107, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-07-14'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934136, 920136, 900108, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-07-02'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934137, 920137, 900108, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-15'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934138, 920138, 900108, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-08-14'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934139, 920139, 900108, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-15'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934140, 920140, 900108, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-07-10'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934141, 920141, 900109, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-03-17'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934142, 920142, 900109, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-12-09'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934143, 920143, 900109, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-06-26'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934144, 920144, 900109, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-03-07'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934145, 920145, 900109, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-08-27'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934146, 920146, 900110, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-14'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934147, 920147, 900110, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-22'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934148, 920148, 900110, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-08-28'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934149, 920149, 900110, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-13'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934150, 920150, 900110, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-11-27'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934151, 920151, 900001, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-09-16'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934152, 920152, 900001, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-05'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934153, 920153, 900001, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-01'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934154, 920154, 900001, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-02-26'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934155, 920155, 900001, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-03-21'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934156, 920156, 900002, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-03-09'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934157, 920157, 900002, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-14'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934158, 920158, 900002, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-07-08'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934159, 920159, 900002, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-12'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934160, 920160, 900002, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-08-13'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934161, 920161, 900003, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-12-09'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934162, 920162, 900003, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-11-02'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934163, 920163, 900003, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-06-07'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934164, 920164, 900003, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-08-01'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934165, 920165, 900003, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-12-14'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934166, 920166, 900004, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-07-15'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934167, 920167, 900004, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-07-14'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934168, 920168, 900004, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-02-23'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934169, 920169, 900004, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-22'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934170, 920170, 900004, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-06-22'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934171, 920171, 900005, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-14'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934172, 920172, 900005, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-08-15'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934173, 920173, 900005, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-12-31'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934174, 920174, 900005, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-01-28'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934175, 920175, 900005, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-07-31'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934176, 920176, 900006, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-12-05'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934177, 920177, 900006, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-04'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934178, 920178, 900006, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-08-05'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934179, 920179, 900006, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-22'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934180, 920180, 900006, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-11-17'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934181, 920181, 900007, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-09-04'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934182, 920182, 900007, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-08-09'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934183, 920183, 900007, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-08-24'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934184, 920184, 900007, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-02-06'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934185, 920185, 900007, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-09-22'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934186, 920186, 900008, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-02-16'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934187, 920187, 900008, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-07-18'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934188, 920188, 900008, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-08-14'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934189, 920189, 900008, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-11-07'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934190, 920190, 900008, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-08-31'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934191, 920191, 900009, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-06-23'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934192, 920192, 900009, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-09-02'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934193, 920193, 900009, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-03-29'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934194, 920194, 900009, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-01-24'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934195, 920195, 900009, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-16'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934196, 920196, 900010, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-01-30'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934197, 920197, 900010, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-01-20'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934198, 920198, 900010, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-02-22'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934199, 920199, 900010, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-02-09'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934200, 920200, 900010, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-18'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934201, 920201, 900101, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-31'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934202, 920202, 900101, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-04-26'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934203, 920203, 900101, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-08-03'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934204, 920204, 900101, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-14'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934205, 920205, 900101, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-08-01'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934206, 920206, 900102, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-08-15'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934207, 920207, 900102, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-19'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934208, 920208, 900102, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-11-29'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934209, 920209, 900102, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-06-03'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934210, 920210, 900102, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-20'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934211, 920211, 900103, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-17'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934212, 920212, 900103, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-03-25'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934213, 920213, 900103, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-11-17'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934214, 920214, 900103, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-01-11'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934215, 920215, 900103, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-08-04'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934216, 920216, 900104, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-08-31'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934217, 920217, 900104, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-09-22'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934218, 920218, 900104, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-01-09'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934219, 920219, 900104, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-11-13'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934220, 920220, 900104, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-04-18'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934221, 920221, 900105, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-02-10'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934222, 920222, 900105, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-04-28'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934223, 920223, 900105, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-12'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934224, 920224, 900105, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-30'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934225, 920225, 900105, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-12-28'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934226, 920226, 900106, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-09-18'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934227, 920227, 900106, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-02-18'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934228, 920228, 900106, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-03-19'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934229, 920229, 900106, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-09-28'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934230, 920230, 900106, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-03-23'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934231, 920231, 900107, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-04-30'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934232, 920232, 900107, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-01-26'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934233, 920233, 900107, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-12-14'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934234, 920234, 900107, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-09-05'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934235, 920235, 900107, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-09-20'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934236, 920236, 900108, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-11-09'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934237, 920237, 900108, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-04-11'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934238, 920238, 900108, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-10-06'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934239, 920239, 900108, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-08-13'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934240, 920240, 900108, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-03-30'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934241, 920241, 900109, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-02-15'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934242, 920242, 900109, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-11-01'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934243, 920243, 900109, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-08-18'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934244, 920244, 900109, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-11-22'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934245, 920245, 900109, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-11'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934246, 920246, 900110, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-06-21'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934247, 920247, 900110, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-01-30'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934248, 920248, 900110, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-05-03'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934249, 920249, 900110, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2025-09-18'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP),
    (934250, 920250, 900110, NULL, 'FIRST_REGISTERED', NULL, COALESCE('2026-04-01'::date::timestamp, CURRENT_TIMESTAMP), NULL, CURRENT_TIMESTAMP);

INSERT INTO ootd_tags (id, ootd_id, item_id, bbox_x, bbox_y, bbox_width, bbox_height, label_text, source, status, confidence, created_at, updated_at)
VALUES
    (926001, 925001, 920001, 0.09000, 0.07000, 0.30000, 0.30000, '빈티지 블루 트러커 데님 재킷', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926002, 925001, 920002, 0.56000, 0.07000, 0.30000, 0.30000, '브라운 체크 울 머플러', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926003, 925001, 920003, 0.12000, 0.43000, 0.30000, 0.39000, '브라운 워크 코튼 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926004, 925001, 920004, 0.56000, 0.43000, 0.30000, 0.39000, '브라운 라운드 워크 부츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926005, 925001, 920005, 0.34000, 0.22000, 0.32000, 0.42000, '카모플라주 로고 볼캡', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926006, 925002, 920006, 0.09000, 0.07000, 0.30000, 0.30000, '브라운 디스트로이드 레더 재킷', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926007, 925002, 920007, 0.56000, 0.07000, 0.30000, 0.30000, '라임 그린 크루넥 니트', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926008, 925002, 920008, 0.12000, 0.43000, 0.30000, 0.39000, '다크 인디고 와이드 데님 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926009, 925002, 920009, 0.56000, 0.43000, 0.30000, 0.39000, '블랙 소프트 숄더백', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926010, 925002, 920010, 0.34000, 0.22000, 0.32000, 0.42000, '브라운 레더 부츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926011, 925003, 920011, 0.09000, 0.07000, 0.30000, 0.30000, '퍼플 브이넥 오버핏 니트', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926012, 925003, 920012, 0.56000, 0.07000, 0.30000, 0.30000, '차콜 체크 레이어드 셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926013, 925003, 920013, 0.12000, 0.43000, 0.30000, 0.39000, '차콜 와이드 트라우저', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926014, 925003, 920014, 0.56000, 0.43000, 0.30000, 0.39000, '브라운 레더 숄더백', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926015, 925003, 920015, 0.34000, 0.22000, 0.32000, 0.42000, '블랙 라운드 토 슈즈', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926016, 925004, 920016, 0.09000, 0.07000, 0.30000, 0.30000, '블루 신칠라 플리스 재킷', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926017, 925004, 920017, 0.56000, 0.07000, 0.30000, 0.30000, '올리브 코튼 가디건', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926018, 925004, 920018, 0.12000, 0.43000, 0.30000, 0.39000, '오프화이트 플레어 미디 스커트', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926019, 925004, 920019, 0.56000, 0.43000, 0.30000, 0.39000, '블랙 1461 더비 슈즈', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926020, 925004, 920020, 0.34000, 0.22000, 0.32000, 0.42000, '블랙 미니 숄더백', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926021, 925005, 920021, 0.09000, 0.07000, 0.30000, 0.30000, '브라운 퀼팅 워크 재킷', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926022, 925005, 920022, 0.56000, 0.07000, 0.30000, 0.30000, '머스터드 레이어드 후디', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926023, 925005, 920023, 0.12000, 0.43000, 0.30000, 0.39000, '네이비 와이드 워크 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926024, 925005, 920024, 0.56000, 0.43000, 0.30000, 0.39000, '블루 NY 로고 볼캡', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926025, 925005, 920025, 0.34000, 0.22000, 0.32000, 0.42000, '블랙 올드스쿨 스니커즈', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926026, 925006, 920026, 0.09000, 0.07000, 0.30000, 0.30000, '레드 깅엄 체크 셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926027, 925006, 920027, 0.56000, 0.07000, 0.30000, 0.30000, '화이트 크루넥 반팔 티셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926028, 925006, 920028, 0.12000, 0.43000, 0.30000, 0.39000, '베이지 와이드 코튼 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926029, 925006, 920029, 0.56000, 0.43000, 0.30000, 0.39000, '블랙 스퀘어 토 더비 슈즈', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926030, 925006, 920030, 0.34000, 0.22000, 0.32000, 0.42000, '멀티컬러 크로셰 비니', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926031, 925007, 920031, 0.09000, 0.07000, 0.30000, 0.30000, '코발트 블루 크루넥 니트', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926032, 925007, 920032, 0.56000, 0.07000, 0.30000, 0.30000, '베이지 와이드 코튼 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926033, 925007, 920033, 0.12000, 0.43000, 0.30000, 0.39000, '네이비 울 머플러', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926034, 925007, 920034, 0.56000, 0.43000, 0.30000, 0.39000, '블랙 스트라이프 글러브', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926035, 925007, 920035, 0.34000, 0.22000, 0.32000, 0.42000, '다크 브라운 플랫 슈즈', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926036, 925008, 920036, 0.09000, 0.07000, 0.30000, 0.30000, '레드 토렌쉘 윈드브레이커', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926037, 925008, 920037, 0.56000, 0.07000, 0.30000, 0.30000, '다크 인디고 와이드 데님 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926038, 925008, 920038, 0.12000, 0.43000, 0.30000, 0.39000, '로열 블루 로고 비니', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926039, 925008, 920039, 0.56000, 0.43000, 0.30000, 0.39000, '블랙 스퀘어 선글라스', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926040, 925008, 920040, 0.34000, 0.22000, 0.32000, 0.42000, '실버 월렛 체인', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926041, 925009, 920041, 0.09000, 0.07000, 0.30000, 0.30000, '블루 베타 쉘 재킷', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926042, 925009, 920042, 0.56000, 0.07000, 0.30000, 0.30000, '다크 워시 데님 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926043, 925009, 920043, 0.12000, 0.43000, 0.30000, 0.39000, '블랙 실드 선글라스', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926044, 925009, 920044, 0.56000, 0.43000, 0.30000, 0.39000, '블랙 나일론 크로스백', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926045, 925009, 920045, 0.34000, 0.22000, 0.32000, 0.42000, '실버 볼드 링', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926046, 925010, 920046, 0.09000, 0.07000, 0.30000, 0.30000, '그레이 와플 긴팔 티셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926047, 925010, 920047, 0.56000, 0.07000, 0.30000, 0.30000, '라이트 블루 와이드 데님 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926048, 925010, 920048, 0.12000, 0.43000, 0.30000, 0.39000, '스카이 블루 로고 비니', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926049, 925010, 920049, 0.56000, 0.43000, 0.30000, 0.39000, '블랙 웨스턴 레더 벨트', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926050, 925010, 920050, 0.34000, 0.22000, 0.32000, 0.42000, '레드 비즈 펜던트 목걸이', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926051, 925011, 920051, 0.09000, 0.07000, 0.30000, 0.30000, '레드 피티드 반팔 니트', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926052, 925011, 920052, 0.56000, 0.07000, 0.30000, 0.30000, '라이트 블루 와이드 데님 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926053, 925011, 920053, 0.12000, 0.43000, 0.30000, 0.39000, '멀티컬러 크로셰 비니', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926054, 925011, 920054, 0.56000, 0.43000, 0.30000, 0.39000, '오프화이트 브레이디드 벨트', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926055, 925011, 920055, 0.34000, 0.22000, 0.32000, 0.42000, '브라운 레더 슈즈', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926056, 925012, 920056, 0.09000, 0.07000, 0.30000, 0.30000, '그린 그래픽 자카드 니트', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926057, 925012, 920057, 0.56000, 0.07000, 0.30000, 0.30000, '멀티컬러 핸드메이드 비니', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926058, 925012, 920058, 0.12000, 0.43000, 0.30000, 0.39000, '라임 페이즐리 스카프', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926059, 925012, 920059, 0.56000, 0.43000, 0.30000, 0.39000, '오프화이트 와이드 코튼 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926060, 925012, 920060, 0.34000, 0.22000, 0.32000, 0.42000, '그린 에어맥스 스니커즈', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926061, 925013, 920061, 0.09000, 0.07000, 0.30000, 0.30000, '그레이 브러시드 모헤어 니트', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926062, 925013, 920062, 0.56000, 0.07000, 0.30000, 0.30000, '블루 레드 스트라이프 머플러', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926063, 925013, 920063, 0.12000, 0.43000, 0.30000, 0.39000, '미드 블루 와이드 데님 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926064, 925013, 920064, 0.56000, 0.43000, 0.30000, 0.39000, '브라운 모카신 슈즈', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926065, 925013, 920065, 0.34000, 0.22000, 0.32000, 0.42000, '실버 라운드 안경', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926066, 925014, 920066, 0.09000, 0.07000, 0.30000, 0.30000, '레드 그래픽 반팔 티셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926067, 925014, 920067, 0.56000, 0.07000, 0.30000, 0.30000, '카모플라주 와이드 카고 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926068, 925014, 920068, 0.12000, 0.43000, 0.30000, 0.39000, '멀티컬러 니트 비니', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926069, 925014, 920069, 0.56000, 0.43000, 0.30000, 0.39000, '그린 그래픽 에코백', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926070, 925014, 920070, 0.34000, 0.22000, 0.32000, 0.42000, '블랙 페니 로퍼', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926071, 925015, 920071, 0.09000, 0.07000, 0.30000, 0.30000, '화이트 오버핏 반팔 티셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926072, 925015, 920072, 0.56000, 0.07000, 0.30000, 0.30000, '라임 옐로 와이드 코튼 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926073, 925015, 920073, 0.12000, 0.43000, 0.30000, 0.39000, '블루 메시지 로고 볼캡', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926074, 925015, 920074, 0.56000, 0.43000, 0.30000, 0.39000, '블랙 랩 선글라스', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926075, 925015, 920075, 0.34000, 0.22000, 0.32000, 0.42000, '브라운 왈라비 슈즈', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926076, 925016, 920076, 0.09000, 0.07000, 0.30000, 0.30000, '라임 옐로 브이넥 니트', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926077, 925016, 920077, 0.56000, 0.07000, 0.30000, 0.30000, '블랙 화이트 스트라이프 긴팔 티셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926078, 925016, 920078, 0.12000, 0.43000, 0.30000, 0.39000, '미드 블루 와이드 데님 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926079, 925016, 920079, 0.56000, 0.43000, 0.30000, 0.39000, '브라운 척 70 스니커즈', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926080, 925016, 920080, 0.34000, 0.22000, 0.32000, 0.42000, '블랙 스퀘어 안경', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926081, 925017, 920081, 0.09000, 0.07000, 0.30000, 0.30000, '그레이 테일러드 블레이저', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926082, 925017, 920082, 0.56000, 0.07000, 0.30000, 0.30000, '라임 브이넥 니트', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926083, 925017, 920083, 0.12000, 0.43000, 0.30000, 0.39000, '블랙 체크 셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926084, 925017, 920084, 0.56000, 0.43000, 0.30000, 0.39000, '그레이 코듀로이 와이드 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926085, 925017, 920085, 0.34000, 0.22000, 0.32000, 0.42000, '블랙 그린 로고 볼캡', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926086, 925018, 920086, 0.09000, 0.07000, 0.30000, 0.30000, '차콜 레귤러핏 반팔 티셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926087, 925018, 920087, 0.56000, 0.07000, 0.30000, 0.30000, '브라운 스트라이프 니트 가디건', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926088, 925018, 920088, 0.12000, 0.43000, 0.30000, 0.39000, '그레이 워시 스트레이트 데님 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926089, 925018, 920089, 0.56000, 0.43000, 0.30000, 0.39000, '브라운 레더 워크 부츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926090, 925018, 920090, 0.34000, 0.22000, 0.32000, 0.42000, '실버 볼드 링', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926091, 925019, 920091, 0.09000, 0.07000, 0.30000, 0.30000, '블랙 리브 집업 가디건', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926092, 925019, 920092, 0.56000, 0.07000, 0.30000, 0.30000, '블랙 울 머플러', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926093, 925019, 920093, 0.12000, 0.43000, 0.30000, 0.39000, '인디고 와이드 데님 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926094, 925019, 920094, 0.56000, 0.43000, 0.30000, 0.39000, '블랙 스웨이드 스니커즈', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926095, 925019, 920095, 0.34000, 0.22000, 0.32000, 0.42000, '블랙 리브 비니', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926096, 925020, 920096, 0.09000, 0.07000, 0.30000, 0.30000, '옐로 브이넥 오버핏 니트', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926097, 925020, 920097, 0.56000, 0.07000, 0.30000, 0.30000, '블랙 화이트 스트라이프 긴팔 티셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926098, 925020, 920098, 0.12000, 0.43000, 0.30000, 0.39000, '미드 블루 와이드 데님 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926099, 925020, 920099, 0.56000, 0.43000, 0.30000, 0.39000, '차콜 척 70 스니커즈', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926100, 925020, 920100, 0.34000, 0.22000, 0.32000, 0.42000, '버건디 데일리 백팩', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926101, 925021, 920101, 0.09000, 0.07000, 0.30000, 0.30000, '헤더 그레이 후드 집업', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926102, 925021, 920102, 0.56000, 0.07000, 0.30000, 0.30000, '화이트 레이어드 반팔 티셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926103, 925021, 920103, 0.12000, 0.43000, 0.30000, 0.39000, '블랙 와이드 트라우저', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926104, 925021, 920104, 0.56000, 0.43000, 0.30000, 0.39000, '로열 블루 볼캡', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926105, 925021, 920105, 0.34000, 0.22000, 0.32000, 0.42000, '블랙 스퀘어 토 로퍼', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926106, 925022, 920106, 0.09000, 0.07000, 0.30000, 0.30000, '다크 워시 크롭 데님 재킷', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926107, 925022, 920107, 0.56000, 0.07000, 0.30000, 0.30000, '블랙 화이트 스트라이프 긴팔 티셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926108, 925022, 920108, 0.12000, 0.43000, 0.30000, 0.39000, '차콜 벌룬 트라우저', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926109, 925022, 920109, 0.56000, 0.43000, 0.30000, 0.39000, '블랙 레더 앵클 부츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926110, 925022, 920110, 0.34000, 0.22000, 0.32000, 0.42000, '실버 체인 목걸이', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926111, 925023, 920111, 0.09000, 0.07000, 0.30000, 0.30000, '세이지 그린 MA-1 블루종', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926112, 925023, 920112, 0.56000, 0.07000, 0.30000, 0.30000, '레드 오버핏 후디', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926113, 925023, 920113, 0.12000, 0.43000, 0.30000, 0.39000, '네이비 사이드라인 트랙 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926114, 925023, 920114, 0.56000, 0.43000, 0.30000, 0.39000, '버건디 NY 버킷햇', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926115, 925023, 920115, 0.34000, 0.22000, 0.32000, 0.42000, '블랙 눕시 뮬 슈즈', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926116, 925024, 920116, 0.09000, 0.07000, 0.30000, 0.30000, '그린 로고 트러커 볼캡', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926117, 925024, 920117, 0.56000, 0.07000, 0.30000, 0.30000, '워시드 블루 디스트로이드 후디', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926118, 925024, 920118, 0.12000, 0.43000, 0.30000, 0.39000, '차콜 와이드 쇼츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926119, 925024, 920119, 0.56000, 0.43000, 0.30000, 0.39000, '브라운 페니 로퍼', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926120, 925024, 920120, 0.34000, 0.22000, 0.32000, 0.42000, '브라운 위빙 더플백', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926121, 925025, 920121, 0.09000, 0.07000, 0.30000, 0.30000, '브라운 체크 울 블레이저', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926122, 925025, 920122, 0.56000, 0.07000, 0.30000, 0.30000, '화이트 크루넥 반팔 티셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926123, 925025, 920123, 0.12000, 0.43000, 0.30000, 0.39000, '다크 브라운 와이드 트라우저', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926124, 925025, 920124, 0.56000, 0.43000, 0.30000, 0.39000, '브라운 페니 로퍼', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926125, 925025, 920125, 0.34000, 0.22000, 0.32000, 0.42000, '브라운 슬림 레더 벨트', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926126, 925026, 920126, 0.09000, 0.07000, 0.30000, 0.30000, '다크 인디고 트러커 데님 재킷', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926127, 925026, 920127, 0.56000, 0.07000, 0.30000, 0.30000, '화이트 크루넥 반팔 티셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926128, 925026, 920128, 0.12000, 0.43000, 0.30000, 0.39000, '베이지 와이드 코튼 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926129, 925026, 920129, 0.56000, 0.43000, 0.30000, 0.39000, '로열 블루 리브 비니', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926130, 925026, 920130, 0.34000, 0.22000, 0.32000, 0.42000, '버건디 레더 로퍼', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926131, 925027, 920131, 0.09000, 0.07000, 0.30000, 0.30000, '오렌지 나일론 윈드브레이커', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926132, 925027, 920132, 0.56000, 0.07000, 0.30000, 0.30000, '그레이 오버핏 후디', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926133, 925027, 920133, 0.12000, 0.43000, 0.30000, 0.39000, '그레이 리버스 위브 스웨트팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926134, 925027, 920134, 0.56000, 0.43000, 0.30000, 0.39000, '블랙 로고 볼캡', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926135, 925027, 920135, 0.34000, 0.22000, 0.32000, 0.42000, '네이비 젤 카야노 러닝화', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926136, 925028, 920136, 0.09000, 0.07000, 0.30000, 0.30000, '네이비 코튼 워크 재킷', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926137, 925028, 920137, 0.56000, 0.07000, 0.30000, 0.30000, '오렌지 크루넥 반팔 티셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926138, 925028, 920138, 0.12000, 0.43000, 0.30000, 0.39000, '올리브 와이드 코튼 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926139, 925028, 920139, 0.56000, 0.43000, 0.30000, 0.39000, '블랙 나일론 백팩', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926140, 925028, 920140, 0.34000, 0.22000, 0.32000, 0.42000, '블랙 로고 볼캡', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926141, 925029, 920141, 0.09000, 0.07000, 0.30000, 0.30000, '워시드 블루 후드 집업', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926142, 925029, 920142, 0.56000, 0.07000, 0.30000, 0.30000, '라이트 워시 와이드 데님 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926143, 925029, 920143, 0.12000, 0.43000, 0.30000, 0.39000, '버건디 로고 볼캡', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926144, 925029, 920144, 0.56000, 0.43000, 0.30000, 0.39000, '블랙 실드 선글라스', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926145, 925029, 920145, 0.34000, 0.22000, 0.32000, 0.42000, '블랙 1906 러닝화', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926146, 925030, 920146, 0.09000, 0.07000, 0.30000, 0.30000, '네이비 플라이트 블루종', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926147, 925030, 920147, 0.56000, 0.07000, 0.30000, 0.30000, '멀티컬러 페어아일 니트', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926148, 925030, 920148, 0.12000, 0.43000, 0.30000, 0.39000, '라이트 그레이 와이드 트라우저', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926149, 925030, 920149, 0.56000, 0.43000, 0.30000, 0.39000, '브라운 태슬 로퍼', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926150, 925030, 920150, 0.34000, 0.22000, 0.32000, 0.42000, '블랙 NY 로고 볼캡', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926151, 925031, 920151, 0.09000, 0.07000, 0.30000, 0.30000, '브라운 플라이트 블루종', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926152, 925031, 920152, 0.56000, 0.07000, 0.30000, 0.30000, '차콜 레이어드 후디', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926153, 925031, 920153, 0.12000, 0.43000, 0.30000, 0.39000, '미드 블루 와이드 데님 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926154, 925031, 920154, 0.56000, 0.43000, 0.30000, 0.39000, '차콜 로고 비니', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926155, 925031, 920155, 0.34000, 0.22000, 0.32000, 0.42000, '브라운 웨스턴 부츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926156, 925032, 920156, 0.09000, 0.07000, 0.30000, 0.30000, '브라이트 블루 크루넥 니트', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926157, 925032, 920157, 0.56000, 0.07000, 0.30000, 0.30000, '오프화이트 와이드 카고 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926158, 925032, 920158, 0.12000, 0.43000, 0.30000, 0.39000, '네이비 NY 볼캡', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926159, 925032, 920159, 0.56000, 0.43000, 0.30000, 0.39000, '블랙 라운드 안경', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926160, 925032, 920160, 0.34000, 0.22000, 0.32000, 0.42000, '그레이 990 스니커즈', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926161, 925033, 920161, 0.09000, 0.07000, 0.30000, 0.30000, '블루 토렌쉘 윈드브레이커', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926162, 925033, 920162, 0.56000, 0.07000, 0.30000, 0.30000, '블랙 워시 와이드 데님 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926163, 925033, 920163, 0.12000, 0.43000, 0.30000, 0.39000, '블랙 레더 부츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926164, 925033, 920164, 0.56000, 0.43000, 0.30000, 0.39000, '블랙 슬림 레더 벨트', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926165, 925033, 920165, 0.34000, 0.22000, 0.32000, 0.42000, '블랙 베이스레이어 긴팔 티셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926166, 925034, 920166, 0.09000, 0.07000, 0.30000, 0.30000, '브라운 디트로이트 워크 재킷', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926167, 925034, 920167, 0.56000, 0.07000, 0.30000, 0.30000, '그레이 오버핏 후디', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926168, 925034, 920168, 0.12000, 0.43000, 0.30000, 0.39000, '블랙 더블니 워크 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926169, 925034, 920169, 0.56000, 0.43000, 0.30000, 0.39000, '블랙 로고 비니', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926170, 925034, 920170, 0.34000, 0.22000, 0.32000, 0.42000, '블랙 척 70 스니커즈', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926171, 925035, 920171, 0.09000, 0.07000, 0.30000, 0.30000, '퍼플 레트로 플리스 재킷', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926172, 925035, 920172, 0.56000, 0.07000, 0.30000, 0.30000, '네이비 트레킹 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926173, 925035, 920173, 0.12000, 0.43000, 0.30000, 0.39000, '블랙 그린 XT-6 스니커즈', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926174, 925035, 920174, 0.56000, 0.43000, 0.30000, 0.39000, '베이지 와이드 브림 햇', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926175, 925035, 920175, 0.34000, 0.22000, 0.32000, 0.42000, '브라운 스포츠 선글라스', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926176, 925036, 920176, 0.09000, 0.07000, 0.30000, 0.30000, '올리브 후디드 아노락 재킷', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926177, 925036, 920177, 0.56000, 0.07000, 0.30000, 0.30000, '그레이 레이어드 후디', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926178, 925036, 920178, 0.12000, 0.43000, 0.30000, 0.39000, '인디고 더블니 데님 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926179, 925036, 920179, 0.56000, 0.43000, 0.30000, 0.39000, '위트 6인치 워커', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926180, 925036, 920180, 0.34000, 0.22000, 0.32000, 0.42000, '블랙 데일리 백팩', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926181, 925037, 920181, 0.09000, 0.07000, 0.30000, 0.30000, '올리브 MA-1 블루종', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926182, 925037, 920182, 0.56000, 0.07000, 0.30000, 0.30000, '네이비 스트라이프 반팔 티셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926183, 925037, 920183, 0.12000, 0.43000, 0.30000, 0.39000, '다크 인디고 와이드 데님 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926184, 925037, 920184, 0.56000, 0.43000, 0.30000, 0.39000, '블랙 레더 로퍼', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926185, 925037, 920185, 0.34000, 0.22000, 0.32000, 0.42000, '블랙 슬림 레더 벨트', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926186, 925038, 920186, 0.09000, 0.07000, 0.30000, 0.30000, '빈티지 블루 초어 재킷', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926187, 925038, 920187, 0.56000, 0.07000, 0.30000, 0.30000, '화이트 크루넥 반팔 티셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926188, 925038, 920188, 0.12000, 0.43000, 0.30000, 0.39000, '미드 블루 와이드 데님 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926189, 925038, 920189, 0.56000, 0.43000, 0.30000, 0.39000, '블랙 페니 로퍼', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926190, 925038, 920190, 0.34000, 0.22000, 0.32000, 0.42000, '브라운 스퀘어 선글라스', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926191, 925039, 920191, 0.09000, 0.07000, 0.30000, 0.30000, '블루 사이드라인 트랙 재킷', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926192, 925039, 920192, 0.56000, 0.07000, 0.30000, 0.30000, '블랙 크루넥 반팔 티셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926193, 925039, 920193, 0.12000, 0.43000, 0.30000, 0.39000, '베이지 와이드 코튼 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926194, 925039, 920194, 0.56000, 0.43000, 0.30000, 0.39000, '멀티 스트라이프 헌팅캡', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926195, 925039, 920195, 0.34000, 0.22000, 0.32000, 0.42000, '블랙 스퀘어 토 부츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926196, 925040, 920196, 0.09000, 0.07000, 0.30000, 0.30000, '그레이 레트로 파일 플리스', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926197, 925040, 920197, 0.56000, 0.07000, 0.30000, 0.30000, '올리브 와이드 카고 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926198, 925040, 920198, 0.12000, 0.43000, 0.30000, 0.39000, '브라운 나일론 슬링백', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926199, 925040, 920199, 0.56000, 0.43000, 0.30000, 0.39000, '그레이 990 스니커즈', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926200, 925040, 920200, 0.34000, 0.22000, 0.32000, 0.42000, '차콜 리브 비니', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926201, 925041, 920201, 0.09000, 0.07000, 0.30000, 0.30000, '인디고 데님 오버올', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926202, 925041, 920202, 0.56000, 0.07000, 0.30000, 0.30000, '그린 네이비 체크 니트', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926203, 925041, 920203, 0.12000, 0.43000, 0.30000, 0.39000, '위트 6인치 워커', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926204, 925041, 920204, 0.56000, 0.43000, 0.30000, 0.39000, '블랙 베이스볼 캡', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926205, 925041, 920205, 0.34000, 0.22000, 0.32000, 0.42000, '블랙 크루넥 긴팔 티셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926206, 925042, 920206, 0.09000, 0.07000, 0.30000, 0.30000, '핑크 신칠라 스냅 플리스', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926207, 925042, 920207, 0.56000, 0.07000, 0.30000, 0.30000, '차콜 와이드 트라우저', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926208, 925042, 920208, 0.12000, 0.43000, 0.30000, 0.39000, '핑크 코튼 볼캡', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926209, 925042, 920209, 0.56000, 0.43000, 0.30000, 0.39000, '블랙 페니 로퍼', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926210, 925042, 920210, 0.34000, 0.22000, 0.32000, 0.42000, '실버 라운드 안경', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926211, 925043, 920211, 0.09000, 0.07000, 0.30000, 0.30000, '올리브 크롭 밀리터리 재킷', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926212, 925043, 920212, 0.56000, 0.07000, 0.30000, 0.30000, '화이트 피케 폴로 셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926213, 925043, 920213, 0.12000, 0.43000, 0.30000, 0.39000, '인디고 롤업 데님 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926214, 925043, 920214, 0.56000, 0.43000, 0.30000, 0.39000, '레오파드 레더 벨트', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926215, 925043, 920215, 0.34000, 0.22000, 0.32000, 0.42000, '블랙 스퀘어 토 부츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926216, 925044, 920216, 0.09000, 0.07000, 0.30000, 0.30000, '블루 와플 긴팔 티셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926217, 925044, 920217, 0.56000, 0.07000, 0.30000, 0.30000, '라이트 그레이 와이드 트라우저', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926218, 925044, 920218, 0.12000, 0.43000, 0.30000, 0.39000, '블루 스트라이프 헌팅캡', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926219, 925044, 920219, 0.56000, 0.43000, 0.30000, 0.39000, '블랙 볼드 프레임 안경', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926220, 925044, 920220, 0.34000, 0.22000, 0.32000, 0.42000, '펄 레이어드 체인 목걸이', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926221, 925045, 920221, 0.09000, 0.07000, 0.30000, 0.30000, '네이비 포켓 반팔 티셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926222, 925045, 920222, 0.56000, 0.07000, 0.30000, 0.30000, '화이트 와플 긴팔 티셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926223, 925045, 920223, 0.12000, 0.43000, 0.30000, 0.39000, '라이트 블루 스트레이트 데님 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926224, 925045, 920224, 0.56000, 0.43000, 0.30000, 0.39000, '화이트 530 러닝화', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926225, 925045, 920225, 0.34000, 0.22000, 0.32000, 0.42000, '베이지 코튼 볼캡', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926226, 925046, 920226, 0.09000, 0.07000, 0.30000, 0.30000, '브라운 페어아일 니트 베스트', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926227, 925046, 920227, 0.56000, 0.07000, 0.30000, 0.30000, '화이트 옥스포드 셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926228, 925046, 920228, 0.12000, 0.43000, 0.30000, 0.39000, '버건디 니트 넥타이', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926229, 925046, 920229, 0.56000, 0.43000, 0.30000, 0.39000, '브라운 플리츠 울 트라우저', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926230, 925046, 920230, 0.34000, 0.22000, 0.32000, 0.42000, '브라운 와이드 레더 벨트', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926231, 925047, 920231, 0.09000, 0.07000, 0.30000, 0.30000, '브라운 숄 칼라 니트 가디건', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926232, 925047, 920232, 0.56000, 0.07000, 0.30000, 0.30000, '베이지 레이어드 후디', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926233, 925047, 920233, 0.12000, 0.43000, 0.30000, 0.39000, '라이트 블루 카고 데님 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926234, 925047, 920234, 0.56000, 0.43000, 0.30000, 0.39000, '버건디 왈라비 슈즈', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926235, 925047, 920235, 0.34000, 0.22000, 0.32000, 0.42000, '실버 심플 링', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926236, 925048, 920236, 0.09000, 0.07000, 0.30000, 0.30000, '라이트 워시 트러커 데님 재킷', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926237, 925048, 920237, 0.56000, 0.07000, 0.30000, 0.30000, '버건디 체크 울 머플러', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926238, 925048, 920238, 0.12000, 0.43000, 0.30000, 0.39000, '차콜 와이드 트라우저', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926239, 925048, 920239, 0.56000, 0.43000, 0.30000, 0.39000, '탄 레더 첼시 부츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926240, 925048, 920240, 0.34000, 0.22000, 0.32000, 0.42000, '네이비 로고 캠프캡', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926241, 925049, 920241, 0.09000, 0.07000, 0.30000, 0.30000, '차콜 워시 반팔 티셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926242, 925049, 920242, 0.56000, 0.07000, 0.30000, 0.30000, '블루 히코리 스트라이프 데님 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926243, 925049, 920243, 0.12000, 0.43000, 0.30000, 0.39000, '옥스블러드 모크토 워크 부츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926244, 925049, 920244, 0.56000, 0.43000, 0.30000, 0.39000, '블루 플라워 반다나', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926245, 925049, 920245, 0.34000, 0.22000, 0.32000, 0.42000, '실버 체인 목걸이', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926246, 925050, 920246, 0.09000, 0.07000, 0.30000, 0.30000, '화이트 레귤러핏 반팔 티셔츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926247, 925050, 920247, 0.56000, 0.07000, 0.30000, 0.30000, '미드 블루 501 스트레이트 데님 팬츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926248, 925050, 920248, 0.12000, 0.43000, 0.30000, 0.39000, '브라운 웨스턴 레더 벨트', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926249, 925050, 920249, 0.56000, 0.43000, 0.30000, 0.39000, '블랙 레더 워크 부츠', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (926250, 925050, 920250, 0.34000, 0.22000, 0.32000, 0.42000, '실버 월렛 체인', 'MANUAL', 'CONFIRMED', NULL::numeric, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO products (id, item_id, seller_user_id, price, shipping_fee, fee_policy, fee_rate, main_image_url, main_image_object_key, measurements, condition_flags, seller_description, product_status, created_at, updated_at)
VALUES
    (924001, 920001, 900101, 130000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_001_item_01.jpg', 'seed/ian/products/ootd_001_item_01.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Levi''s의 빈티지 블루 트러커 데님 재킷입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
가볍게 걸치기 좋고 다양한 이너와 매치하기 편합니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '200 minutes', CURRENT_TIMESTAMP - INTERVAL '200 minutes'),
    (924002, 920005, 900101, 45000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_001_item_05.jpg', 'seed/ian/products/ootd_001_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Patagonia의 카모플라주 로고 볼캡입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '199 minutes', CURRENT_TIMESTAMP - INTERVAL '199 minutes'),
    (924003, 920008, 900102, 75000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_002_item_03.jpg', 'seed/ian/products/ootd_002_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Levi''s의 다크 인디고 와이드 데님 팬츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
다양한 상의와 매치하기 쉬운 실용적인 실루엣입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '198 minutes', CURRENT_TIMESTAMP - INTERVAL '198 minutes'),
    (924004, 920009, 900102, 185000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_002_item_04.jpg', 'seed/ian/products/ootd_002_item_04.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Lemaire의 블랙 소프트 숄더백입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '197 minutes', CURRENT_TIMESTAMP - INTERVAL '197 minutes'),
    (924005, 920010, 900102, 75000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_002_item_05.jpg', 'seed/ian/products/ootd_002_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Dr. Martens의 브라운 레더 부츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '196 minutes', CURRENT_TIMESTAMP - INTERVAL '196 minutes'),
    (924006, 920014, 900103, 70000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_003_item_04.jpg', 'seed/ian/products/ootd_003_item_04.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'A.P.C.의 브라운 레더 숄더백입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '195 minutes', CURRENT_TIMESTAMP - INTERVAL '195 minutes'),
    (924007, 920015, 900103, 75000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_003_item_05.jpg', 'seed/ian/products/ootd_003_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Camper의 블랙 라운드 토 슈즈입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '194 minutes', CURRENT_TIMESTAMP - INTERVAL '194 minutes'),
    (924008, 920017, 900104, 70000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_004_item_02.jpg', 'seed/ian/products/ootd_004_item_02.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Ralph Lauren의 올리브 코튼 가디건입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '193 minutes', CURRENT_TIMESTAMP - INTERVAL '193 minutes'),
    (924009, 920018, 900104, 45000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_004_item_03.jpg', 'seed/ian/products/ootd_004_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'COS의 오프화이트 플레어 미디 스커트입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '192 minutes', CURRENT_TIMESTAMP - INTERVAL '192 minutes'),
    (924010, 920020, 900104, 185000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_004_item_05.jpg', 'seed/ian/products/ootd_004_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Lemaire의 블랙 미니 숄더백입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '191 minutes', CURRENT_TIMESTAMP - INTERVAL '191 minutes'),
    (924011, 920025, 900105, 85000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_005_item_05.jpg', 'seed/ian/products/ootd_005_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Vans의 블랙 올드스쿨 스니커즈입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '190 minutes', CURRENT_TIMESTAMP - INTERVAL '190 minutes'),
    (924012, 920028, 900106, 85000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_006_item_03.jpg', 'seed/ian/products/ootd_006_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Carhartt WIP의 베이지 와이드 코튼 팬츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
다양한 상의와 매치하기 쉬운 실용적인 실루엣입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '189 minutes', CURRENT_TIMESTAMP - INTERVAL '189 minutes'),
    (924013, 920030, 900106, 30000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_006_item_05.jpg', 'seed/ian/products/ootd_006_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'KANGOL의 멀티컬러 크로셰 비니입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '188 minutes', CURRENT_TIMESTAMP - INTERVAL '188 minutes'),
    (924014, 920031, 900107, 40000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_007_item_01.jpg', 'seed/ian/products/ootd_007_item_01.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'COS의 코발트 블루 크루넥 니트입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '187 minutes', CURRENT_TIMESTAMP - INTERVAL '187 minutes'),
    (924015, 920034, 900107, 30000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_007_item_04.jpg', 'seed/ian/products/ootd_007_item_04.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'THE NORTH FACE의 블랙 스트라이프 글러브입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '186 minutes', CURRENT_TIMESTAMP - INTERVAL '186 minutes'),
    (924016, 920035, 900107, 80000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_007_item_05.jpg', 'seed/ian/products/ootd_007_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Camper의 다크 브라운 플랫 슈즈입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '185 minutes', CURRENT_TIMESTAMP - INTERVAL '185 minutes'),
    (924017, 920036, 900108, 140000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_008_item_01.jpg', 'seed/ian/products/ootd_008_item_01.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Patagonia의 레드 토렌쉘 윈드브레이커입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
가볍게 걸치기 좋고 다양한 이너와 매치하기 편합니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '184 minutes', CURRENT_TIMESTAMP - INTERVAL '184 minutes'),
    (924018, 920037, 900108, 95000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_008_item_02.jpg', 'seed/ian/products/ootd_008_item_02.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Levi''s의 다크 인디고 와이드 데님 팬츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
다양한 상의와 매치하기 쉬운 실용적인 실루엣입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '183 minutes', CURRENT_TIMESTAMP - INTERVAL '183 minutes'),
    (924019, 920038, 900108, 45000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_008_item_03.jpg', 'seed/ian/products/ootd_008_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Stussy의 로열 블루 로고 비니입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '182 minutes', CURRENT_TIMESTAMP - INTERVAL '182 minutes'),
    (924020, 920041, 900109, 150000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_009_item_01.jpg', 'seed/ian/products/ootd_009_item_01.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Arc''teryx의 블루 베타 쉘 재킷입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
가볍게 걸치기 좋고 다양한 이너와 매치하기 편합니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '181 minutes', CURRENT_TIMESTAMP - INTERVAL '181 minutes'),
    (924021, 920043, 900109, 30000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_009_item_03.jpg', 'seed/ian/products/ootd_009_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Oakley의 블랙 실드 선글라스입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '180 minutes', CURRENT_TIMESTAMP - INTERVAL '180 minutes'),
    (924022, 920045, 900109, 45000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_009_item_05.jpg', 'seed/ian/products/ootd_009_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Maison Margiela의 실버 볼드 링입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '179 minutes', CURRENT_TIMESTAMP - INTERVAL '179 minutes'),
    (924023, 920049, 900110, 35000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_010_item_04.jpg', 'seed/ian/products/ootd_010_item_04.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'A.P.C.의 블랙 웨스턴 레더 벨트입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '178 minutes', CURRENT_TIMESTAMP - INTERVAL '178 minutes'),
    (924024, 920050, 900110, 55000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_010_item_05.jpg', 'seed/ian/products/ootd_010_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Vivienne Westwood의 레드 비즈 펜던트 목걸이입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '177 minutes', CURRENT_TIMESTAMP - INTERVAL '177 minutes'),
    (924025, 920051, 900001, 125000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_011_item_01.jpg', 'seed/ian/products/ootd_011_item_01.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Miu Miu의 레드 피티드 반팔 니트입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '176 minutes', CURRENT_TIMESTAMP - INTERVAL '176 minutes'),
    (924026, 920052, 900001, 75000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_011_item_02.jpg', 'seed/ian/products/ootd_011_item_02.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Levi''s의 라이트 블루 와이드 데님 팬츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
다양한 상의와 매치하기 쉬운 실용적인 실루엣입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '175 minutes', CURRENT_TIMESTAMP - INTERVAL '175 minutes'),
    (924027, 920053, 900001, 30000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_011_item_03.jpg', 'seed/ian/products/ootd_011_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'KANGOL의 멀티컬러 크로셰 비니입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '174 minutes', CURRENT_TIMESTAMP - INTERVAL '174 minutes'),
    (924028, 920054, 900001, 30000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_011_item_04.jpg', 'seed/ian/products/ootd_011_item_04.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'A.P.C.의 오프화이트 브레이디드 벨트입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '173 minutes', CURRENT_TIMESTAMP - INTERVAL '173 minutes'),
    (924029, 920055, 900001, 70000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_011_item_05.jpg', 'seed/ian/products/ootd_011_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Clarks의 브라운 레더 슈즈입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '172 minutes', CURRENT_TIMESTAMP - INTERVAL '172 minutes'),
    (924030, 920056, 900002, 75000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_012_item_01.jpg', 'seed/ian/products/ootd_012_item_01.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'KAPITAL의 그린 그래픽 자카드 니트입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '171 minutes', CURRENT_TIMESTAMP - INTERVAL '171 minutes'),
    (924031, 920060, 900002, 80000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_012_item_05.jpg', 'seed/ian/products/ootd_012_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Nike의 그린 에어맥스 스니커즈입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '170 minutes', CURRENT_TIMESTAMP - INTERVAL '170 minutes'),
    (924032, 920061, 900003, 105000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_013_item_01.jpg', 'seed/ian/products/ootd_013_item_01.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Acne Studios의 그레이 브러시드 모헤어 니트입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '169 minutes', CURRENT_TIMESTAMP - INTERVAL '169 minutes'),
    (924033, 920062, 900003, 45000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_013_item_02.jpg', 'seed/ian/products/ootd_013_item_02.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Polo Ralph Lauren의 블루 레드 스트라이프 머플러입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '168 minutes', CURRENT_TIMESTAMP - INTERVAL '168 minutes'),
    (924034, 920063, 900003, 75000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_013_item_03.jpg', 'seed/ian/products/ootd_013_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Levi''s의 미드 블루 와이드 데님 팬츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
다양한 상의와 매치하기 쉬운 실용적인 실루엣입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '167 minutes', CURRENT_TIMESTAMP - INTERVAL '167 minutes'),
    (924035, 920064, 900003, 70000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_013_item_04.jpg', 'seed/ian/products/ootd_013_item_04.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'L.L.Bean의 브라운 모카신 슈즈입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '166 minutes', CURRENT_TIMESTAMP - INTERVAL '166 minutes'),
    (924036, 920066, 900004, 65000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_014_item_01.jpg', 'seed/ian/products/ootd_014_item_01.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Stussy의 레드 그래픽 반팔 티셔츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '165 minutes', CURRENT_TIMESTAMP - INTERVAL '165 minutes'),
    (924037, 920070, 900004, 80000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_014_item_05.jpg', 'seed/ian/products/ootd_014_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'G.H.BASS의 블랙 페니 로퍼입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '164 minutes', CURRENT_TIMESTAMP - INTERVAL '164 minutes'),
    (924038, 920072, 900005, 80000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_015_item_02.jpg', 'seed/ian/products/ootd_015_item_02.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Dickies의 라임 옐로 와이드 코튼 팬츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
다양한 상의와 매치하기 쉬운 실용적인 실루엣입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '163 minutes', CURRENT_TIMESTAMP - INTERVAL '163 minutes'),
    (924039, 920074, 900005, 30000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_015_item_04.jpg', 'seed/ian/products/ootd_015_item_04.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Oakley의 블랙 랩 선글라스입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '162 minutes', CURRENT_TIMESTAMP - INTERVAL '162 minutes'),
    (924040, 920075, 900005, 85000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_015_item_05.jpg', 'seed/ian/products/ootd_015_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Clarks의 브라운 왈라비 슈즈입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '161 minutes', CURRENT_TIMESTAMP - INTERVAL '161 minutes'),
    (924041, 920076, 900006, 50000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_016_item_01.jpeg', 'seed/ian/products/ootd_016_item_01.jpeg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'COS의 라임 옐로 브이넥 니트입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '160 minutes', CURRENT_TIMESTAMP - INTERVAL '160 minutes'),
    (924042, 920077, 900006, 30000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_016_item_02.jpg', 'seed/ian/products/ootd_016_item_02.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'UNIQLO U의 블랙 화이트 스트라이프 긴팔 티셔츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '159 minutes', CURRENT_TIMESTAMP - INTERVAL '159 minutes'),
    (924043, 920081, 900007, 135000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_017_item_01.jpg', 'seed/ian/products/ootd_017_item_01.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Theory의 그레이 테일러드 블레이저입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
가볍게 걸치기 좋고 다양한 이너와 매치하기 편합니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '158 minutes', CURRENT_TIMESTAMP - INTERVAL '158 minutes'),
    (924044, 920084, 900007, 75000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_017_item_04.jpg', 'seed/ian/products/ootd_017_item_04.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Levi''s의 그레이 코듀로이 와이드 팬츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
다양한 상의와 매치하기 쉬운 실용적인 실루엣입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '157 minutes', CURRENT_TIMESTAMP - INTERVAL '157 minutes'),
    (924045, 920085, 900007, 50000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_017_item_05.jpg', 'seed/ian/products/ootd_017_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'adidas의 블랙 그린 로고 볼캡입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '156 minutes', CURRENT_TIMESTAMP - INTERVAL '156 minutes'),
    (924046, 920086, 900008, 130000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_018_item_01.jpg', 'seed/ian/products/ootd_018_item_01.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Lemaire의 차콜 레귤러핏 반팔 티셔츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '155 minutes', CURRENT_TIMESTAMP - INTERVAL '155 minutes'),
    (924047, 920090, 900008, 50000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_018_item_05.jpg', 'seed/ian/products/ootd_018_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Maison Margiela의 실버 볼드 링입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '154 minutes', CURRENT_TIMESTAMP - INTERVAL '154 minutes'),
    (924048, 920093, 900009, 95000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_019_item_03.jpg', 'seed/ian/products/ootd_019_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Levi''s의 인디고 와이드 데님 팬츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
다양한 상의와 매치하기 쉬운 실용적인 실루엣입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '153 minutes', CURRENT_TIMESTAMP - INTERVAL '153 minutes'),
    (924049, 920096, 900010, 40000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_020_item_01.jpg', 'seed/ian/products/ootd_020_item_01.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'COS의 옐로 브이넥 오버핏 니트입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '152 minutes', CURRENT_TIMESTAMP - INTERVAL '152 minutes'),
    (924050, 920098, 900010, 95000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_020_item_03.jpg', 'seed/ian/products/ootd_020_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Levi''s의 미드 블루 와이드 데님 팬츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
다양한 상의와 매치하기 쉬운 실용적인 실루엣입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '151 minutes', CURRENT_TIMESTAMP - INTERVAL '151 minutes'),
    (924051, 920099, 900010, 75000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_020_item_04.jpg', 'seed/ian/products/ootd_020_item_04.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Converse의 차콜 척 70 스니커즈입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '150 minutes', CURRENT_TIMESTAMP - INTERVAL '150 minutes'),
    (924052, 920100, 900010, 75000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_020_item_05.jpg', 'seed/ian/products/ootd_020_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'EASTPAK의 버건디 데일리 백팩입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '149 minutes', CURRENT_TIMESTAMP - INTERVAL '149 minutes'),
    (924053, 920103, 900101, 55000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_021_item_03.jpeg', 'seed/ian/products/ootd_021_item_03.jpeg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'COS의 블랙 와이드 트라우저입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
다양한 상의와 매치하기 쉬운 실용적인 실루엣입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '148 minutes', CURRENT_TIMESTAMP - INTERVAL '148 minutes'),
    (924054, 920104, 900101, 30000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_021_item_04.jpg', 'seed/ian/products/ootd_021_item_04.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'New Era의 로열 블루 볼캡입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '147 minutes', CURRENT_TIMESTAMP - INTERVAL '147 minutes'),
    (924055, 920105, 900101, 80000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_021_item_05.jpg', 'seed/ian/products/ootd_021_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Dr. Martens의 블랙 스퀘어 토 로퍼입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '146 minutes', CURRENT_TIMESTAMP - INTERVAL '146 minutes'),
    (924056, 920106, 900102, 135000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_022_item_01.jpg', 'seed/ian/products/ootd_022_item_01.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Levi''s의 다크 워시 크롭 데님 재킷입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
가볍게 걸치기 좋고 다양한 이너와 매치하기 편합니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '145 minutes', CURRENT_TIMESTAMP - INTERVAL '145 minutes'),
    (924057, 920108, 900102, 60000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_022_item_03.jpg', 'seed/ian/products/ootd_022_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'COS의 차콜 벌룬 트라우저입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
다양한 상의와 매치하기 쉬운 실용적인 실루엣입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '144 minutes', CURRENT_TIMESTAMP - INTERVAL '144 minutes'),
    (924058, 920109, 900102, 70000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_022_item_04.jpg', 'seed/ian/products/ootd_022_item_04.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Dr. Martens의 블랙 레더 앵클 부츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '143 minutes', CURRENT_TIMESTAMP - INTERVAL '143 minutes'),
    (924059, 920111, 900103, 140000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_023_item_01.jpg', 'seed/ian/products/ootd_023_item_01.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Alpha Industries의 세이지 그린 MA-1 블루종입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
가볍게 걸치기 좋고 다양한 이너와 매치하기 편합니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '142 minutes', CURRENT_TIMESTAMP - INTERVAL '142 minutes'),
    (924060, 920112, 900103, 75000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_023_item_02.jpg', 'seed/ian/products/ootd_023_item_02.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Champion의 레드 오버핏 후디입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '141 minutes', CURRENT_TIMESTAMP - INTERVAL '141 minutes'),
    (924061, 920113, 900103, 55000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_023_item_03.jpg', 'seed/ian/products/ootd_023_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Needles의 네이비 사이드라인 트랙 팬츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
다양한 상의와 매치하기 쉬운 실용적인 실루엣입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '140 minutes', CURRENT_TIMESTAMP - INTERVAL '140 minutes'),
    (924062, 920114, 900103, 30000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_023_item_04.jpg', 'seed/ian/products/ootd_023_item_04.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'New Era의 버건디 NY 버킷햇입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '139 minutes', CURRENT_TIMESTAMP - INTERVAL '139 minutes'),
    (924063, 920117, 900104, 70000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_024_item_02.jpg', 'seed/ian/products/ootd_024_item_02.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Stussy의 워시드 블루 디스트로이드 후디입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '138 minutes', CURRENT_TIMESTAMP - INTERVAL '138 minutes'),
    (924064, 920118, 900104, 75000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_024_item_03.jpg', 'seed/ian/products/ootd_024_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Dickies의 차콜 와이드 쇼츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
다양한 상의와 매치하기 쉬운 실용적인 실루엣입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '137 minutes', CURRENT_TIMESTAMP - INTERVAL '137 minutes'),
    (924065, 920119, 900104, 70000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_024_item_04.jpg', 'seed/ian/products/ootd_024_item_04.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'G.H.BASS의 브라운 페니 로퍼입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '136 minutes', CURRENT_TIMESTAMP - INTERVAL '136 minutes'),
    (924066, 920120, 900104, 100000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_024_item_05.jpg', 'seed/ian/products/ootd_024_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Polo Ralph Lauren의 브라운 위빙 더플백입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '135 minutes', CURRENT_TIMESTAMP - INTERVAL '135 minutes'),
    (924067, 920122, 900105, 35000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_025_item_02.jpg', 'seed/ian/products/ootd_025_item_02.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Hanes의 화이트 크루넥 반팔 티셔츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '134 minutes', CURRENT_TIMESTAMP - INTERVAL '134 minutes'),
    (924068, 920128, 900106, 80000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_026_item_03.jpg', 'seed/ian/products/ootd_026_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Dickies의 베이지 와이드 코튼 팬츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
다양한 상의와 매치하기 쉬운 실용적인 실루엣입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '133 minutes', CURRENT_TIMESTAMP - INTERVAL '133 minutes'),
    (924069, 920129, 900106, 45000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_026_item_04.jpg', 'seed/ian/products/ootd_026_item_04.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Stussy의 로열 블루 리브 비니입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '132 minutes', CURRENT_TIMESTAMP - INTERVAL '132 minutes'),
    (924070, 920130, 900106, 75000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_026_item_05.jpg', 'seed/ian/products/ootd_026_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'G.H.BASS의 버건디 레더 로퍼입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '131 minutes', CURRENT_TIMESTAMP - INTERVAL '131 minutes'),
    (924071, 920132, 900107, 75000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_027_item_02.jpg', 'seed/ian/products/ootd_027_item_02.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Champion의 그레이 오버핏 후디입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '130 minutes', CURRENT_TIMESTAMP - INTERVAL '130 minutes'),
    (924072, 920135, 900107, 70000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_027_item_05.jpg', 'seed/ian/products/ootd_027_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'ASICS의 네이비 젤 카야노 러닝화입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '129 minutes', CURRENT_TIMESTAMP - INTERVAL '129 minutes'),
    (924073, 920138, 900108, 85000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_028_item_03.jpg', 'seed/ian/products/ootd_028_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Dickies의 올리브 와이드 코튼 팬츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
다양한 상의와 매치하기 쉬운 실용적인 실루엣입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '128 minutes', CURRENT_TIMESTAMP - INTERVAL '128 minutes'),
    (924074, 920140, 900108, 45000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_028_item_05.jpg', 'seed/ian/products/ootd_028_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Stussy의 블랙 로고 볼캡입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '127 minutes', CURRENT_TIMESTAMP - INTERVAL '127 minutes'),
    (924075, 920143, 900109, 35000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_029_item_03.jpg', 'seed/ian/products/ootd_029_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Chrome Hearts의 버건디 로고 볼캡입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '126 minutes', CURRENT_TIMESTAMP - INTERVAL '126 minutes'),
    (924076, 920144, 900109, 30000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_029_item_04.jpg', 'seed/ian/products/ootd_029_item_04.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Oakley의 블랙 실드 선글라스입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '125 minutes', CURRENT_TIMESTAMP - INTERVAL '125 minutes'),
    (924077, 920146, 900110, 125000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_030_item_01.jpg', 'seed/ian/products/ootd_030_item_01.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Schott NYC의 네이비 플라이트 블루종입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
가볍게 걸치기 좋고 다양한 이너와 매치하기 편합니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '124 minutes', CURRENT_TIMESTAMP - INTERVAL '124 minutes'),
    (924078, 920150, 900110, 30000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_030_item_05.jpg', 'seed/ian/products/ootd_030_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'New Era의 블랙 NY 로고 볼캡입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '123 minutes', CURRENT_TIMESTAMP - INTERVAL '123 minutes'),
    (924079, 920152, 900001, 65000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_031_item_02.jpg', 'seed/ian/products/ootd_031_item_02.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Champion의 차콜 레이어드 후디입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '122 minutes', CURRENT_TIMESTAMP - INTERVAL '122 minutes'),
    (924080, 920153, 900001, 95000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_031_item_03.jpg', 'seed/ian/products/ootd_031_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Levi''s의 미드 블루 와이드 데님 팬츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
다양한 상의와 매치하기 쉬운 실용적인 실루엣입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '121 minutes', CURRENT_TIMESTAMP - INTERVAL '121 minutes'),
    (924081, 920154, 900001, 25000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_031_item_04.jpg', 'seed/ian/products/ootd_031_item_04.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Lacoste의 차콜 로고 비니입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '120 minutes', CURRENT_TIMESTAMP - INTERVAL '120 minutes'),
    (924082, 920155, 900001, 85000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_031_item_05.jpg', 'seed/ian/products/ootd_031_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Tony Lama의 브라운 웨스턴 부츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '119 minutes', CURRENT_TIMESTAMP - INTERVAL '119 minutes'),
    (924083, 920156, 900002, 50000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_032_item_01.jpg', 'seed/ian/products/ootd_032_item_01.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'COS의 브라이트 블루 크루넥 니트입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '118 minutes', CURRENT_TIMESTAMP - INTERVAL '118 minutes'),
    (924084, 920161, 900003, 140000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_033_item_01.jpg', 'seed/ian/products/ootd_033_item_01.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Patagonia의 블루 토렌쉘 윈드브레이커입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
가볍게 걸치기 좋고 다양한 이너와 매치하기 편합니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '117 minutes', CURRENT_TIMESTAMP - INTERVAL '117 minutes'),
    (924085, 920166, 900004, 150000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_034_item_01.jpg', 'seed/ian/products/ootd_034_item_01.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Carhartt WIP의 브라운 디트로이트 워크 재킷입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
가볍게 걸치기 좋고 다양한 이너와 매치하기 편합니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '116 minutes', CURRENT_TIMESTAMP - INTERVAL '116 minutes'),
    (924086, 920167, 900004, 75000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_034_item_02.jpg', 'seed/ian/products/ootd_034_item_02.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Champion의 그레이 오버핏 후디입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '115 minutes', CURRENT_TIMESTAMP - INTERVAL '115 minutes'),
    (924087, 920168, 900004, 85000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_034_item_03.jpg', 'seed/ian/products/ootd_034_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Carhartt WIP의 블랙 더블니 워크 팬츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
다양한 상의와 매치하기 쉬운 실용적인 실루엣입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '114 minutes', CURRENT_TIMESTAMP - INTERVAL '114 minutes'),
    (924088, 920169, 900004, 45000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_034_item_04.jpg', 'seed/ian/products/ootd_034_item_04.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Stussy의 블랙 로고 비니입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '113 minutes', CURRENT_TIMESTAMP - INTERVAL '113 minutes'),
    (924089, 920170, 900004, 70000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_034_item_05.jpg', 'seed/ian/products/ootd_034_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Converse의 블랙 척 70 스니커즈입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '112 minutes', CURRENT_TIMESTAMP - INTERVAL '112 minutes'),
    (924090, 920173, 900005, 80000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_035_item_03.jpg', 'seed/ian/products/ootd_035_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Salomon의 블랙 그린 XT-6 스니커즈입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '111 minutes', CURRENT_TIMESTAMP - INTERVAL '111 minutes'),
    (924091, 920175, 900005, 35000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_035_item_05.jpg', 'seed/ian/products/ootd_035_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Oakley의 브라운 스포츠 선글라스입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '110 minutes', CURRENT_TIMESTAMP - INTERVAL '110 minutes'),
    (924092, 920180, 900006, 75000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_036_item_05.jpg', 'seed/ian/products/ootd_036_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'A.P.C.의 블랙 데일리 백팩입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '109 minutes', CURRENT_TIMESTAMP - INTERVAL '109 minutes'),
    (924093, 920182, 900007, 35000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_037_item_02.jpg', 'seed/ian/products/ootd_037_item_02.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'UNIQLO U의 네이비 스트라이프 반팔 티셔츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '108 minutes', CURRENT_TIMESTAMP - INTERVAL '108 minutes'),
    (924094, 920183, 900007, 90000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_037_item_03.jpg', 'seed/ian/products/ootd_037_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Levi''s의 다크 인디고 와이드 데님 팬츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
다양한 상의와 매치하기 쉬운 실용적인 실루엣입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '107 minutes', CURRENT_TIMESTAMP - INTERVAL '107 minutes'),
    (924095, 920186, 900008, 155000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_038_item_01.jpg', 'seed/ian/products/ootd_038_item_01.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Aimé Leon Dore의 빈티지 블루 초어 재킷입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
가볍게 걸치기 좋고 다양한 이너와 매치하기 편합니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '106 minutes', CURRENT_TIMESTAMP - INTERVAL '106 minutes'),
    (924096, 920187, 900008, 30000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_038_item_02.jpg', 'seed/ian/products/ootd_038_item_02.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'UNIQLO U의 화이트 크루넥 반팔 티셔츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '105 minutes', CURRENT_TIMESTAMP - INTERVAL '105 minutes'),
    (924097, 920189, 900008, 75000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_038_item_04.jpg', 'seed/ian/products/ootd_038_item_04.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'G.H.BASS의 블랙 페니 로퍼입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '104 minutes', CURRENT_TIMESTAMP - INTERVAL '104 minutes'),
    (924098, 920191, 900009, 150000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_039_item_01.jpg', 'seed/ian/products/ootd_039_item_01.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'adidas의 블루 사이드라인 트랙 재킷입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
가볍게 걸치기 좋고 다양한 이너와 매치하기 편합니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '103 minutes', CURRENT_TIMESTAMP - INTERVAL '103 minutes'),
    (924099, 920192, 900009, 35000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_039_item_02.jpg', 'seed/ian/products/ootd_039_item_02.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'UNIQLO U의 블랙 크루넥 반팔 티셔츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '102 minutes', CURRENT_TIMESTAMP - INTERVAL '102 minutes'),
    (924100, 920193, 900009, 75000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_039_item_03.jpg', 'seed/ian/products/ootd_039_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Carhartt WIP의 베이지 와이드 코튼 팬츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
다양한 상의와 매치하기 쉬운 실용적인 실루엣입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '101 minutes', CURRENT_TIMESTAMP - INTERVAL '101 minutes'),
    (924101, 920194, 900009, 25000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_039_item_04.jpeg', 'seed/ian/products/ootd_039_item_04.jpeg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'KANGOL의 멀티 스트라이프 헌팅캡입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '100 minutes', CURRENT_TIMESTAMP - INTERVAL '100 minutes'),
    (924102, 920196, 900010, 135000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_040_item_01.jpg', 'seed/ian/products/ootd_040_item_01.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Patagonia의 그레이 레트로 파일 플리스입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
가볍게 걸치기 좋고 다양한 이너와 매치하기 편합니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '99 minutes', CURRENT_TIMESTAMP - INTERVAL '99 minutes'),
    (924103, 920197, 900010, 95000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_040_item_02.jpg', 'seed/ian/products/ootd_040_item_02.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Carhartt WIP의 올리브 와이드 카고 팬츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
다양한 상의와 매치하기 쉬운 실용적인 실루엣입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '98 minutes', CURRENT_TIMESTAMP - INTERVAL '98 minutes'),
    (924104, 920198, 900010, 65000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_040_item_03.jpg', 'seed/ian/products/ootd_040_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'PORTER의 브라운 나일론 슬링백입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '97 minutes', CURRENT_TIMESTAMP - INTERVAL '97 minutes'),
    (924105, 920199, 900010, 80000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_040_item_04.jpg', 'seed/ian/products/ootd_040_item_04.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'New Balance의 그레이 990 스니커즈입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '96 minutes', CURRENT_TIMESTAMP - INTERVAL '96 minutes'),
    (924106, 920200, 900010, 50000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_040_item_05.jpg', 'seed/ian/products/ootd_040_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Stussy의 차콜 리브 비니입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '95 minutes', CURRENT_TIMESTAMP - INTERVAL '95 minutes'),
    (924107, 920206, 900102, 145000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_042_item_01.jpg', 'seed/ian/products/ootd_042_item_01.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Patagonia의 핑크 신칠라 스냅 플리스입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
가볍게 걸치기 좋고 다양한 이너와 매치하기 편합니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '94 minutes', CURRENT_TIMESTAMP - INTERVAL '94 minutes'),
    (924108, 920207, 900102, 80000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_042_item_02.jpg', 'seed/ian/products/ootd_042_item_02.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Theory의 차콜 와이드 트라우저입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
다양한 상의와 매치하기 쉬운 실용적인 실루엣입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '93 minutes', CURRENT_TIMESTAMP - INTERVAL '93 minutes'),
    (924109, 920208, 900102, 50000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_042_item_03.jpg', 'seed/ian/products/ootd_042_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Polo Ralph Lauren의 핑크 코튼 볼캡입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '92 minutes', CURRENT_TIMESTAMP - INTERVAL '92 minutes'),
    (924110, 920213, 900103, 80000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_043_item_03.jpeg', 'seed/ian/products/ootd_043_item_03.jpeg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Levi''s의 인디고 롤업 데님 팬츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
다양한 상의와 매치하기 쉬운 실용적인 실루엣입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '91 minutes', CURRENT_TIMESTAMP - INTERVAL '91 minutes'),
    (924111, 920215, 900103, 85000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_043_item_05.jpg', 'seed/ian/products/ootd_043_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Maison Margiela의 블랙 스퀘어 토 부츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '90 minutes', CURRENT_TIMESTAMP - INTERVAL '90 minutes'),
    (924112, 920216, 900104, 60000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_044_item_01.jpg', 'seed/ian/products/ootd_044_item_01.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Aimé Leon Dore의 블루 와플 긴팔 티셔츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '89 minutes', CURRENT_TIMESTAMP - INTERVAL '89 minutes'),
    (924113, 920217, 900104, 50000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_044_item_02.jpg', 'seed/ian/products/ootd_044_item_02.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'COS의 라이트 그레이 와이드 트라우저입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
다양한 상의와 매치하기 쉬운 실용적인 실루엣입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '88 minutes', CURRENT_TIMESTAMP - INTERVAL '88 minutes'),
    (924114, 920218, 900104, 30000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_044_item_03.jpg', 'seed/ian/products/ootd_044_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'KANGOL의 블루 스트라이프 헌팅캡입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '87 minutes', CURRENT_TIMESTAMP - INTERVAL '87 minutes'),
    (924115, 920219, 900104, 25000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_044_item_04.jpg', 'seed/ian/products/ootd_044_item_04.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Gentle Monster의 블랙 볼드 프레임 안경입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '86 minutes', CURRENT_TIMESTAMP - INTERVAL '86 minutes'),
    (924116, 920221, 900105, 35000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_045_item_01.jpg', 'seed/ian/products/ootd_045_item_01.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Hanes의 네이비 포켓 반팔 티셔츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '85 minutes', CURRENT_TIMESTAMP - INTERVAL '85 minutes'),
    (924117, 920224, 900105, 70000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_045_item_04.jpg', 'seed/ian/products/ootd_045_item_04.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'New Balance의 화이트 530 러닝화입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '84 minutes', CURRENT_TIMESTAMP - INTERVAL '84 minutes'),
    (924118, 920225, 900105, 50000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_045_item_05.jpg', 'seed/ian/products/ootd_045_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Polo Ralph Lauren의 베이지 코튼 볼캡입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '83 minutes', CURRENT_TIMESTAMP - INTERVAL '83 minutes'),
    (924119, 920227, 900106, 65000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_046_item_02.jpg', 'seed/ian/products/ootd_046_item_02.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Ralph Lauren의 화이트 옥스포드 셔츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '82 minutes', CURRENT_TIMESTAMP - INTERVAL '82 minutes'),
    (924120, 920228, 900106, 45000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_046_item_03.jpg', 'seed/ian/products/ootd_046_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Polo Ralph Lauren의 버건디 니트 넥타이입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '81 minutes', CURRENT_TIMESTAMP - INTERVAL '81 minutes'),
    (924121, 920231, 900107, 65000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_047_item_01.jpg', 'seed/ian/products/ootd_047_item_01.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'RRL의 브라운 숄 칼라 니트 가디건입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '80 minutes', CURRENT_TIMESTAMP - INTERVAL '80 minutes'),
    (924122, 920232, 900107, 70000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_047_item_02.jpg', 'seed/ian/products/ootd_047_item_02.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Champion의 베이지 레이어드 후디입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '79 minutes', CURRENT_TIMESTAMP - INTERVAL '79 minutes'),
    (924123, 920233, 900107, 85000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_047_item_03.jpg', 'seed/ian/products/ootd_047_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Carhartt WIP의 라이트 블루 카고 데님 팬츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
다양한 상의와 매치하기 쉬운 실용적인 실루엣입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '78 minutes', CURRENT_TIMESTAMP - INTERVAL '78 minutes'),
    (924124, 920234, 900107, 75000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_047_item_04.jpg', 'seed/ian/products/ootd_047_item_04.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Clarks의 버건디 왈라비 슈즈입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '77 minutes', CURRENT_TIMESTAMP - INTERVAL '77 minutes'),
    (924125, 920237, 900108, 45000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_048_item_02.jpg', 'seed/ian/products/ootd_048_item_02.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Polo Ralph Lauren의 버건디 체크 울 머플러입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '76 minutes', CURRENT_TIMESTAMP - INTERVAL '76 minutes'),
    (924126, 920238, 900108, 90000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_048_item_03.jpg', 'seed/ian/products/ootd_048_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Theory의 차콜 와이드 트라우저입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
다양한 상의와 매치하기 쉬운 실용적인 실루엣입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '75 minutes', CURRENT_TIMESTAMP - INTERVAL '75 minutes'),
    (924127, 920239, 900108, 65000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_048_item_04.jpg', 'seed/ian/products/ootd_048_item_04.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Blundstone의 탄 레더 첼시 부츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '74 minutes', CURRENT_TIMESTAMP - INTERVAL '74 minutes'),
    (924128, 920243, 900109, 80000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_049_item_03.jpg', 'seed/ian/products/ootd_049_item_03.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Red Wing의 옥스블러드 모크토 워크 부츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '73 minutes', CURRENT_TIMESTAMP - INTERVAL '73 minutes'),
    (924129, 920245, 900109, 55000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_049_item_05.jpg', 'seed/ian/products/ootd_049_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Chrome Hearts의 실버 체인 목걸이입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '72 minutes', CURRENT_TIMESTAMP - INTERVAL '72 minutes'),
    (924130, 920246, 900110, 30000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_050_item_01.jpg', 'seed/ian/products/ootd_050_item_01.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'UNIQLO U의 화이트 레귤러핏 반팔 티셔츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
단독 착용이나 레이어드 코디에 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '71 minutes', CURRENT_TIMESTAMP - INTERVAL '71 minutes'),
    (924131, 920249, 900110, 85000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_050_item_04.jpg', 'seed/ian/products/ootd_050_item_04.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Red Wing의 블랙 레더 워크 부츠입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '70 minutes', CURRENT_TIMESTAMP - INTERVAL '70 minutes'),
    (924132, 920250, 900110, 45000, 3000, 'SELLER_PAYS', 0.0600, '/files/seed/ian/products/ootd_050_item_05.jpg', 'seed/ian/products/ootd_050_item_05.jpg', NULL::jsonb, '{"stain":false,"scratch":false,"washed":true,"smell":false}'::jsonb, 'Chrome Hearts의 실버 월렛 체인입니다.
전체적으로 깔끔하게 관리해 착용감이 적은 편이에요.
데일리 코디에 포인트로 활용하기 좋은 제품입니다.
사이즈 확인 후 편하게 문의해주세요.', 'ON_SALE', CURRENT_TIMESTAMP - INTERVAL '69 minutes', CURRENT_TIMESTAMP - INTERVAL '69 minutes');

INSERT INTO product_attached_ootds (id, product_id, ootd_id, created_at)
VALUES
    (933001, 924001, 925001, CURRENT_TIMESTAMP),
    (933002, 924002, 925001, CURRENT_TIMESTAMP),
    (933003, 924003, 925002, CURRENT_TIMESTAMP),
    (933004, 924004, 925002, CURRENT_TIMESTAMP),
    (933005, 924005, 925002, CURRENT_TIMESTAMP),
    (933006, 924006, 925003, CURRENT_TIMESTAMP),
    (933007, 924007, 925003, CURRENT_TIMESTAMP),
    (933008, 924008, 925004, CURRENT_TIMESTAMP),
    (933009, 924009, 925004, CURRENT_TIMESTAMP),
    (933010, 924010, 925004, CURRENT_TIMESTAMP),
    (933011, 924011, 925005, CURRENT_TIMESTAMP),
    (933012, 924012, 925006, CURRENT_TIMESTAMP),
    (933013, 924013, 925006, CURRENT_TIMESTAMP),
    (933014, 924014, 925007, CURRENT_TIMESTAMP),
    (933015, 924015, 925007, CURRENT_TIMESTAMP),
    (933016, 924016, 925007, CURRENT_TIMESTAMP),
    (933017, 924017, 925008, CURRENT_TIMESTAMP),
    (933018, 924018, 925008, CURRENT_TIMESTAMP),
    (933019, 924019, 925008, CURRENT_TIMESTAMP),
    (933020, 924020, 925009, CURRENT_TIMESTAMP),
    (933021, 924021, 925009, CURRENT_TIMESTAMP),
    (933022, 924022, 925009, CURRENT_TIMESTAMP),
    (933023, 924023, 925010, CURRENT_TIMESTAMP),
    (933024, 924024, 925010, CURRENT_TIMESTAMP),
    (933025, 924025, 925011, CURRENT_TIMESTAMP),
    (933026, 924026, 925011, CURRENT_TIMESTAMP),
    (933027, 924027, 925011, CURRENT_TIMESTAMP),
    (933028, 924028, 925011, CURRENT_TIMESTAMP),
    (933029, 924029, 925011, CURRENT_TIMESTAMP),
    (933030, 924030, 925012, CURRENT_TIMESTAMP),
    (933031, 924031, 925012, CURRENT_TIMESTAMP),
    (933032, 924032, 925013, CURRENT_TIMESTAMP),
    (933033, 924033, 925013, CURRENT_TIMESTAMP),
    (933034, 924034, 925013, CURRENT_TIMESTAMP),
    (933035, 924035, 925013, CURRENT_TIMESTAMP),
    (933036, 924036, 925014, CURRENT_TIMESTAMP),
    (933037, 924037, 925014, CURRENT_TIMESTAMP),
    (933038, 924038, 925015, CURRENT_TIMESTAMP),
    (933039, 924039, 925015, CURRENT_TIMESTAMP),
    (933040, 924040, 925015, CURRENT_TIMESTAMP),
    (933041, 924041, 925016, CURRENT_TIMESTAMP),
    (933042, 924042, 925016, CURRENT_TIMESTAMP),
    (933043, 924043, 925017, CURRENT_TIMESTAMP),
    (933044, 924044, 925017, CURRENT_TIMESTAMP),
    (933045, 924045, 925017, CURRENT_TIMESTAMP),
    (933046, 924046, 925018, CURRENT_TIMESTAMP),
    (933047, 924047, 925018, CURRENT_TIMESTAMP),
    (933048, 924048, 925019, CURRENT_TIMESTAMP),
    (933049, 924049, 925020, CURRENT_TIMESTAMP),
    (933050, 924050, 925020, CURRENT_TIMESTAMP),
    (933051, 924051, 925020, CURRENT_TIMESTAMP),
    (933052, 924052, 925020, CURRENT_TIMESTAMP),
    (933053, 924053, 925021, CURRENT_TIMESTAMP),
    (933054, 924054, 925021, CURRENT_TIMESTAMP),
    (933055, 924055, 925021, CURRENT_TIMESTAMP),
    (933056, 924056, 925022, CURRENT_TIMESTAMP),
    (933057, 924057, 925022, CURRENT_TIMESTAMP),
    (933058, 924058, 925022, CURRENT_TIMESTAMP),
    (933059, 924059, 925023, CURRENT_TIMESTAMP),
    (933060, 924060, 925023, CURRENT_TIMESTAMP),
    (933061, 924061, 925023, CURRENT_TIMESTAMP),
    (933062, 924062, 925023, CURRENT_TIMESTAMP),
    (933063, 924063, 925024, CURRENT_TIMESTAMP),
    (933064, 924064, 925024, CURRENT_TIMESTAMP),
    (933065, 924065, 925024, CURRENT_TIMESTAMP),
    (933066, 924066, 925024, CURRENT_TIMESTAMP),
    (933067, 924067, 925025, CURRENT_TIMESTAMP),
    (933068, 924068, 925026, CURRENT_TIMESTAMP),
    (933069, 924069, 925026, CURRENT_TIMESTAMP),
    (933070, 924070, 925026, CURRENT_TIMESTAMP),
    (933071, 924071, 925027, CURRENT_TIMESTAMP),
    (933072, 924072, 925027, CURRENT_TIMESTAMP),
    (933073, 924073, 925028, CURRENT_TIMESTAMP),
    (933074, 924074, 925028, CURRENT_TIMESTAMP),
    (933075, 924075, 925029, CURRENT_TIMESTAMP),
    (933076, 924076, 925029, CURRENT_TIMESTAMP),
    (933077, 924077, 925030, CURRENT_TIMESTAMP),
    (933078, 924078, 925030, CURRENT_TIMESTAMP),
    (933079, 924079, 925031, CURRENT_TIMESTAMP),
    (933080, 924080, 925031, CURRENT_TIMESTAMP),
    (933081, 924081, 925031, CURRENT_TIMESTAMP),
    (933082, 924082, 925031, CURRENT_TIMESTAMP),
    (933083, 924083, 925032, CURRENT_TIMESTAMP),
    (933084, 924084, 925033, CURRENT_TIMESTAMP),
    (933085, 924085, 925034, CURRENT_TIMESTAMP),
    (933086, 924086, 925034, CURRENT_TIMESTAMP),
    (933087, 924087, 925034, CURRENT_TIMESTAMP),
    (933088, 924088, 925034, CURRENT_TIMESTAMP),
    (933089, 924089, 925034, CURRENT_TIMESTAMP),
    (933090, 924090, 925035, CURRENT_TIMESTAMP),
    (933091, 924091, 925035, CURRENT_TIMESTAMP),
    (933092, 924092, 925036, CURRENT_TIMESTAMP),
    (933093, 924093, 925037, CURRENT_TIMESTAMP),
    (933094, 924094, 925037, CURRENT_TIMESTAMP),
    (933095, 924095, 925038, CURRENT_TIMESTAMP),
    (933096, 924096, 925038, CURRENT_TIMESTAMP),
    (933097, 924097, 925038, CURRENT_TIMESTAMP),
    (933098, 924098, 925039, CURRENT_TIMESTAMP),
    (933099, 924099, 925039, CURRENT_TIMESTAMP),
    (933100, 924100, 925039, CURRENT_TIMESTAMP),
    (933101, 924101, 925039, CURRENT_TIMESTAMP),
    (933102, 924102, 925040, CURRENT_TIMESTAMP),
    (933103, 924103, 925040, CURRENT_TIMESTAMP),
    (933104, 924104, 925040, CURRENT_TIMESTAMP),
    (933105, 924105, 925040, CURRENT_TIMESTAMP),
    (933106, 924106, 925040, CURRENT_TIMESTAMP),
    (933107, 924107, 925042, CURRENT_TIMESTAMP),
    (933108, 924108, 925042, CURRENT_TIMESTAMP),
    (933109, 924109, 925042, CURRENT_TIMESTAMP),
    (933110, 924110, 925043, CURRENT_TIMESTAMP),
    (933111, 924111, 925043, CURRENT_TIMESTAMP),
    (933112, 924112, 925044, CURRENT_TIMESTAMP),
    (933113, 924113, 925044, CURRENT_TIMESTAMP),
    (933114, 924114, 925044, CURRENT_TIMESTAMP),
    (933115, 924115, 925044, CURRENT_TIMESTAMP),
    (933116, 924116, 925045, CURRENT_TIMESTAMP),
    (933117, 924117, 925045, CURRENT_TIMESTAMP),
    (933118, 924118, 925045, CURRENT_TIMESTAMP),
    (933119, 924119, 925046, CURRENT_TIMESTAMP),
    (933120, 924120, 925046, CURRENT_TIMESTAMP),
    (933121, 924121, 925047, CURRENT_TIMESTAMP),
    (933122, 924122, 925047, CURRENT_TIMESTAMP),
    (933123, 924123, 925047, CURRENT_TIMESTAMP),
    (933124, 924124, 925047, CURRENT_TIMESTAMP),
    (933125, 924125, 925048, CURRENT_TIMESTAMP),
    (933126, 924126, 925048, CURRENT_TIMESTAMP),
    (933127, 924127, 925048, CURRENT_TIMESTAMP),
    (933128, 924128, 925049, CURRENT_TIMESTAMP),
    (933129, 924129, 925049, CURRENT_TIMESTAMP),
    (933130, 924130, 925050, CURRENT_TIMESTAMP),
    (933131, 924131, 925050, CURRENT_TIMESTAMP),
    (933132, 924132, 925050, CURRENT_TIMESTAMP);

SELECT setval(pg_get_serial_sequence('items', 'id'), GREATEST((SELECT MAX(id) FROM items), 920250), TRUE);
SELECT setval(pg_get_serial_sequence('item_histories', 'id'), GREATEST((SELECT MAX(id) FROM item_histories), 934250), TRUE);
SELECT setval(pg_get_serial_sequence('ootds', 'id'), GREATEST((SELECT MAX(id) FROM ootds), 925050), TRUE);
SELECT setval(pg_get_serial_sequence('ootd_tags', 'id'), GREATEST((SELECT MAX(id) FROM ootd_tags), 926250), TRUE);
SELECT setval(pg_get_serial_sequence('products', 'id'), GREATEST((SELECT MAX(id) FROM products), 924132), TRUE);
SELECT setval(pg_get_serial_sequence('product_attached_ootds', 'id'), GREATEST((SELECT MAX(id) FROM product_attached_ootds), 933132), TRUE);
