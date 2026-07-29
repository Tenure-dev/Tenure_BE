ALTER TABLE chat_rooms
    DROP CONSTRAINT uk_chat_rooms_item_buyer_seller,
    ADD COLUMN is_closed BOOLEAN NOT NULL DEFAULT FALSE;
