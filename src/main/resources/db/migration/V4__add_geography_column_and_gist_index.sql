-- Migrazione V4: Aggiunge colonna geography e indice GIST per ricerche spaziali ottimizzate

-- Abilita l'estensione PostGIS se non è già presente
CREATE EXTENSION IF NOT EXISTS postgis;

-- Aggiunge la colonna geography di tipo GEOGRAPHY(Point, 4326)
ALTER TABLE address ADD COLUMN IF NOT EXISTS geography GEOGRAPHY(Point, 4326);

-- Popola la nuova colonna geography con i dati esistenti da latitude e longitude
-- Usa ST_Point per creare un punto geografico dalle coordinate esistenti
UPDATE address 
SET geography = ST_SetSRID(ST_MakePoint(longitude::double precision, latitude::double precision), 4326)::geography
WHERE latitude IS NOT NULL AND longitude IS NOT NULL AND geography IS NULL;

-- Crea l'indice GIST sulla colonna geography per ottimizzare le ricerche spaziali
CREATE INDEX IF NOT EXISTS idx_address_geography_gist ON address USING GIST (geography);

-- Commento: Le colonne latitude e longitude vengono mantenute per compatibilità con l'API esistente
-- ma possono essere rimosse in futuro una volta che tutti i servizi sono migrati all'uso della colonna geography