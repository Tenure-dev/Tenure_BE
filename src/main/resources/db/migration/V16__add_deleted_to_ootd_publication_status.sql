ALTER TABLE ootds DROP CONSTRAINT ck_ootds_publication_status;
ALTER TABLE ootds ADD CONSTRAINT ck_ootds_publication_status
    CHECK (publication_status IN ('ACTIVE', 'ARCHIVED', 'DELETED'));
