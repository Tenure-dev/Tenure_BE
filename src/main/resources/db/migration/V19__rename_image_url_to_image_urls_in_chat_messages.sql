ALTER TABLE chat_messages
    DROP COLUMN image_url,
    ADD COLUMN image_urls TEXT;
