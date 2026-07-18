-- notifications 표시용 필드 추가
ALTER TABLE notifications
    ADD COLUMN image_url VARCHAR(500),
    ADD COLUMN brand_name VARCHAR(100),
    ADD COLUMN item_name VARCHAR(200),
    ADD COLUMN sender_username VARCHAR(50);

-- title 컬럼 제거
ALTER TABLE notifications DROP COLUMN title;

-- NotificationType CHECK 제약 재설정
ALTER TABLE notifications DROP CONSTRAINT ck_notifications_type;

ALTER TABLE notifications
    ADD CONSTRAINT ck_notifications_type CHECK (
        type IN (

            'PURCHASE_INTENT_SENT',
            'PURCHASE_OFFER_SENT',
            'REQUEST_ACCEPTED',
            'REQUEST_REJECTED',
            'REQUEST_CANCELED_BY_REQUESTER',
            'REQUEST_EXPIRED',
            'REQUEST_CANCELED_BY_COMPETING_ACCEPTANCE',
            'REQUEST_CANCELED_BY_ITEM_DELETE',
            'TRADE_CANCELED',
            'SHIPMENT_REGISTERED',
            'DELIVERY_COMPLETED',
            'PURCHASE_CONFIRMED',
            'SETTLEMENT_COMPLETED',
            'TRADE_COMPLETED',
            'WISH_CREATED',
            'PRODUCT_CREATED',
            'PRODUCT_RETURNED_TO_UNSOLD',
            'PRODUCT_SOLD',
            'PRODUCT_PRICE_CHANGED',
            'CHAT_MESSAGE_CREATED',
            'FOLLOW_CREATED'
        )
    );
