-- Aggiunta della colonna discriminatoria alla tabella property
ALTER TABLE property ADD COLUMN property_type VARCHAR(50);

-- Aggiornamento delle esistenti righe con i tipi di proprietà appropriati
UPDATE property SET property_type = 'RESIDENTIAL' WHERE id IN (SELECT id FROM residential_property);
UPDATE property SET property_type = 'COMMERCIAL' WHERE id IN (SELECT id FROM commercial_property);
UPDATE property SET property_type = 'GARAGE' WHERE id IN (SELECT id FROM garage);
UPDATE property SET property_type = 'LAND' WHERE id IN (SELECT id FROM land);

-- Aggiunta dei campi mancanti alla tabella property
ALTER TABLE property ADD COLUMN created_at TIMESTAMP;
ALTER TABLE property ADD COLUMN additional_features TEXT;

-- Impostazione dei valori predefiniti per i nuovi campi
UPDATE property SET created_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;

-- Rimozione delle chiavi esterne dalle tabelle delle sottoclassi
ALTER TABLE residential_property DROP CONSTRAINT fk_residentialproperty_property;
ALTER TABLE commercial_property DROP CONSTRAINT fk_commercialproperty_property;
ALTER TABLE garage DROP CONSTRAINT fk_garage_property;
ALTER TABLE land DROP CONSTRAINT fk_land_property;

-- Rimozione delle colonne id_property da tutte le tabelle delle sottoclassi
ALTER TABLE residential_property DROP COLUMN id;
ALTER TABLE commercial_property DROP COLUMN id_property;
ALTER TABLE garage DROP COLUMN id_property;
ALTER TABLE land DROP COLUMN id;

-- Aggiunta dei commenti per documentare lo schema
COMMENT ON COLUMN property.property_type IS 'Discriminatore per il tipo di proprietà (RESIDENTIAL, COMMERCIAL, GARAGE, LAND)';
COMMENT ON COLUMN property.created_at IS 'Data e ora di creazione della proprietà';
COMMENT ON COLUMN property.additional_features IS 'Caratteristiche aggiuntive della proprietà in formato testo';