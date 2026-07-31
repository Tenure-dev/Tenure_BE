ALTER TABLE purchase_offers
    ADD COLUMN trade_request_note VARCHAR(500);

ALTER TABLE trades
    ADD COLUMN trade_request_note VARCHAR(500);
