-- Use the tagged OOTD image as a fallback representative image for unsold Ian seed items.

UPDATE items i
SET
    representative_image_url = o.image_url,
    representative_image_object_key = o.image_object_key,
    updated_at = CURRENT_TIMESTAMP
FROM ootd_tags t
JOIN ootds o ON o.id = t.ootd_id
WHERE i.id = t.item_id
  AND i.id BETWEEN 920001 AND 920250
  AND i.item_status = 'OWNED'
  AND (i.representative_image_url IS NULL OR i.representative_image_url = '');
