-- Costanti per evitare duplicazione di valori letterali
DECLARE commercial_category CONSTANT VARCHAR := 'commercial_property';
DECLARE residential_category CONSTANT VARCHAR := 'residential_property';
DECLARE land_category CONSTANT VARCHAR := 'land';
DECLARE garage_category CONSTANT VARCHAR := 'garage';

INSERT INTO dieti_estates.address (latitude, longitude, city, country, province, street, street_number) VALUES
    (45.123456, 12.123456, 'Fictional City', 'Country1', 'Fictional Province', 'Imaginary Street', '123'),
    (45.654321, 12.654321, 'Dreamland', 'Country2', 'Dream Province', 'Fantasy Avenue', '456'),
    (45.789012, 12.789012, 'Wonderland', 'Country3', 'Wonder Province', 'Magic Boulevard', '789'),
    (45.345678, 12.345678, 'Mystic Town', 'Country4', 'Mystic Province', 'Enchanted Lane', '101'),
    (45.987654, 12.987654, 'Fairy Village', 'Country5', 'Fairy Province', 'Pixie Path', '202');

INSERT INTO dieti_estates.agency (id_address, name) VALUES (
    1, 'The Hobbits'
);

-- Agent000@
-- 123Pass!
INSERT INTO dieti_estates.user (id_agency, is_agent, is_manager, email, password, username, first_name, last_name)  VALUES
    (1, TRUE, TRUE, 'agent@gmail.com', '$2a$12$K5tOHLOh7nXYVuTukNf.cu0e.sf01a918YOWSjNYjBSUL0tWnfK4y', 'Agent01', 'Smith', 'Agente'),
    (NULL, FALSE, FALSE, 'prova@gmail.com', '$2a$12$sSATIARk3Q51ZvMV1DsSIeLEXLyKlYKyWGKNk.ZURQwlGAjUMmEVu', 'User44', 'Fab', 'Apu');

INSERT INTO dieti_estates.property_category (category, subcategory, is_active) VALUES
    (commercial_category, 'commercial_local', TRUE),
    (commercial_category, 'laboratory', TRUE),
    (commercial_category, 'commercial_activity', TRUE),
    (commercial_category, 'storehouse', TRUE),
    (commercial_category, 'depot', TRUE),
    (residential_category, 'apartment', TRUE),
    (residential_category, 'penthouse', TRUE),
    (residential_category, 'attic', TRUE),
    (residential_category, 'loft', TRUE),
    (residential_category, 'detached_house', TRUE),
    (residential_category, 'villa', TRUE),
    (residential_category, 'terraced_house', TRUE),
    (residential_category, 'country_house', TRUE),
    (land_category, 'agricultural', TRUE),
    (land_category, 'building', TRUE),
    (land_category, 'non-building', TRUE),
    (garage_category, 'garage', TRUE),
    (garage_category, 'parking space', TRUE)
;

INSERT INTO dieti_estates.contract (name, is_active) VALUES
    ('sale', TRUE),
    ('rental', TRUE)
;

INSERT INTO dieti_estates.property (
    area, price, year_built, id_address, id_agent, id_contract, id_property_category, status, energy_rating, description) VALUES
    (150.0, 700000.00, 2024, 1, 1, 1, 10, 'UNDER_CONSTRUCTION', 'A1', 'Beautiful 3-bedroom house with modern amenities and a spacious garden.'),
    (85.0, 400000.00, 2002, 2, 1, 1, 12, 'RENOVATED', 'D', 'Country house with rustic charm, featuring a large kitchen and scenic views.'),
    (200.0, 1200000.00, 2018, 3, 1, 1, 10, 'NEW', 'A1', 'Luxurious villa with a private pool and high-end finishes.'),
    (60.0, 350000.00, 2025, 4, 1, 1, 6, 'UNDER_CONSTRUCTION', 'B', 'Modern apartment in the city center with easy access to public transport.'),
    (100.0, 500000.00, 2012, 5, 1, 1, 7, 'TO_BE_RENOVATED', 'B', 'Spacious penthouse with panoramic city views and a large terrace.');

INSERT INTO dieti_estates.garage (id_property, has_surveillance, number_of_floors) VALUES (
    4, TRUE, 1
);

INSERT INTO dieti_estates.commercial_property (id_property, floor, number_of_bathrooms, number_of_floors, number_of_rooms, numero_vetrine, wheelchair_access) VALUES (
    1, 0, 2, 1, 3, 1, TRUE
);