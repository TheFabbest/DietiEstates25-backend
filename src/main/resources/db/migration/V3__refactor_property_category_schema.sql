-- V3: Refactor property_category schema to implement canonical naming approach
-- Drop and recreate the table with the new simplified schema

-- Drop the existing table (no data to preserve)
-- DROP TABLE IF EXISTS PropertyCategory CASCADE;

-- -- Create the new PropertyCategory table with the optimized schema
-- CREATE TABLE PropertyCategory (
--     id BIGSERIAL PRIMARY KEY,
    
--     -- Canonical discriminator for JPA inheritance and property type grouping
--     -- Values: RESIDENTIAL, COMMERCIAL, LAND, GARAGE
--     property_type VARCHAR(50) NOT NULL,
    
--     -- Specific category name shown to users (Apartment, Villa, Office, Shop, etc.)
--     name VARCHAR(255) NOT NULL UNIQUE,
    
--     -- Active flag for soft deletion/enabling categories
--     is_active BOOLEAN NOT NULL DEFAULT true,
    
--     -- Audit fields from BaseEntity pattern
--     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
-- );

-- Add indexes for efficient lookups
CREATE INDEX idx_property_category_property_type ON PropertyCategory(property_type);
CREATE INDEX idx_property_category_name ON PropertyCategory(name);
CREATE INDEX idx_property_category_active ON PropertyCategory(is_active);

-- Add constraint to ensure property_type contains only valid values
-- ALTER TABLE PropertyCategory ADD CONSTRAINT chk_property_type_valid 
-- CHECK (property_type IN ('RESIDENTIAL', 'COMMERCIAL', 'LAND', 'GARAGE'));

-- Insert initial data according to the new schema
INSERT INTO PropertyCategory (property_type, name, is_active) VALUES
-- Residential properties
('RESIDENTIAL', 'Apartment', true),
('RESIDENTIAL', 'Villa', true),
('RESIDENTIAL', 'Penthouse', true),
('RESIDENTIAL', 'Townhouse', true),

-- Commercial properties  
('COMMERCIAL', 'Office', true),
('COMMERCIAL', 'Shop', true),
('COMMERCIAL', 'Warehouse', true),
('COMMERCIAL', 'Restaurant', true),

-- Land properties
('LAND', 'Agricultural Land', true),
('LAND', 'Building Plot', true),
('LAND', 'Industrial Land', true),

-- Garage properties
('GARAGE', 'Single Garage', true),
('GARAGE', 'Double Garage', true),
('GARAGE', 'Parking Space', true);