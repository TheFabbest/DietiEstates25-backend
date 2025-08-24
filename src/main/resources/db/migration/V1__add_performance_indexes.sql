CREATE INDEX idx_properties_price ON properties (price);
CREATE INDEX idx_properties_area ON properties (area);
CREATE INDEX idx_properties_year_built ON properties (year_built);
CREATE INDEX idx_properties_status ON properties (status);
CREATE INDEX idx_properties_property_category_id ON properties (property_category_id);
CREATE INDEX idx_properties_contract_id ON properties (contract_id);

CREATE INDEX idx_offers_user_id ON offers (user_id);

CREATE INDEX idx_visits_user_id ON visits (user_id);

CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX idx_properties_description_gin ON properties USING gin (description gin_trgm_ops);