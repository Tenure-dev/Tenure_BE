ALTER TABLE notifications DROP CONSTRAINT ck_notifications_category;
ALTER TABLE notifications ADD CONSTRAINT ck_notifications_category
    CHECK (category IN ('INTEREST', 'NEEDS_ACTION', 'ITEM_NEWS', 'TRADE_STATUS'));
