ALTER TABLE trades
    ADD COLUMN delivery_receiver_name VARCHAR(50) NOT NULL DEFAULT '',
    ADD COLUMN delivery_phone VARCHAR(20) NOT NULL DEFAULT '',
    ADD COLUMN delivery_address_line1 VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN delivery_address_line2 VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN delivery_postal_code VARCHAR(10),
    ADD COLUMN delivery_request_note VARCHAR(300);

ALTER TABLE trades
    ALTER COLUMN delivery_receiver_name DROP DEFAULT,
    ALTER COLUMN delivery_phone DROP DEFAULT,
    ALTER COLUMN delivery_address_line1 DROP DEFAULT,
    ALTER COLUMN delivery_address_line2 DROP DEFAULT;

ALTER TABLE trades
    ADD CONSTRAINT uk_trades_source_type_source_id UNIQUE (source_type, source_id);
