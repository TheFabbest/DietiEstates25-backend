ALTER TABLE property
ADD COLUMN image_directory_ulid VARCHAR(26) NOT NULL,
ADD COLUMN number_of_images INT NOT NULL DEFAULT 1;

ALTER TABLE property
ADD CONSTRAINT uk_property_image_directory_ulid UNIQUE (image_directory_ulid),
ADD CONSTRAINT chk_property_number_of_images CHECK (number_of_images >= 1);