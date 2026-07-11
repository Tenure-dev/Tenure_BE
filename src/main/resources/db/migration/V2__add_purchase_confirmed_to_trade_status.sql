ALTER TABLE trades
    DROP CONSTRAINT ck_trades_status;

ALTER TABLE trades
    ADD CONSTRAINT ck_trades_status CHECK (
        status IN ('PAID', 'SHIPPED', 'DELIVERED', 'PURCHASE_CONFIRMED', 'SETTLED', 'COMPLETED', 'TRANSFERRED')
    );
