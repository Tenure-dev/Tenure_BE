ALTER TABLE products
    ADD COLUMN fee_rate NUMERIC(5, 4) NOT NULL DEFAULT 0.0600;

ALTER TABLE products
    DROP CONSTRAINT ck_products_product_status;

ALTER TABLE products
    ADD CONSTRAINT ck_products_product_status CHECK (
        product_status IN ('ON_SALE', 'TRADING', 'SOLD', 'HIDDEN')
    );

ALTER TABLE purchase_intents
    ADD COLUMN fee_rate_snapshot NUMERIC(5, 4) NOT NULL DEFAULT 0.0600,
    ADD COLUMN delivery_receiver_name VARCHAR(50) NOT NULL DEFAULT '',
    ADD COLUMN delivery_phone VARCHAR(20) NOT NULL DEFAULT '',
    ADD COLUMN delivery_address_line1 VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN delivery_address_line2 VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN delivery_postal_code VARCHAR(10),
    ADD COLUMN delivery_request_note VARCHAR(300);

ALTER TABLE purchase_intents
    ALTER COLUMN delivery_receiver_name DROP DEFAULT,
    ALTER COLUMN delivery_phone DROP DEFAULT,
    ALTER COLUMN delivery_address_line1 DROP DEFAULT,
    ALTER COLUMN delivery_address_line2 DROP DEFAULT;

ALTER TABLE purchase_offers
    ADD COLUMN fee_rate_snapshot NUMERIC(5, 4) NOT NULL DEFAULT 0.0600,
    ADD COLUMN delivery_receiver_name VARCHAR(50) NOT NULL DEFAULT '',
    ADD COLUMN delivery_phone VARCHAR(20) NOT NULL DEFAULT '',
    ADD COLUMN delivery_address_line1 VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN delivery_address_line2 VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN delivery_postal_code VARCHAR(10),
    ADD COLUMN delivery_request_note VARCHAR(300);

ALTER TABLE purchase_offers
    ALTER COLUMN delivery_receiver_name DROP DEFAULT,
    ALTER COLUMN delivery_phone DROP DEFAULT,
    ALTER COLUMN delivery_address_line1 DROP DEFAULT,
    ALTER COLUMN delivery_address_line2 DROP DEFAULT;
