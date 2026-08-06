ALTER TABLE users
    ADD COLUMN profile_image_object_key VARCHAR(700);

ALTER TABLE items
    ADD COLUMN representative_image_object_key VARCHAR(700);

ALTER TABLE ootds
    ADD COLUMN image_object_key VARCHAR(700);

ALTER TABLE products
    ADD COLUMN main_image_object_key VARCHAR(700);
