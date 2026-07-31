ALTER TABLE ootds ADD COLUMN hot_score DOUBLE PRECISION NOT NULL DEFAULT 0.0;

UPDATE ootds
SET hot_score = (COALESCE(save_count, 0) * 3.0 + COALESCE(heart_count, 0) * 2.0 + COALESCE(view_count, 0) * 0.05 + 1.0)
                / SQRT(1.0 + EXTRACT(EPOCH FROM (NOW() - created_at)) / 86400.0);
