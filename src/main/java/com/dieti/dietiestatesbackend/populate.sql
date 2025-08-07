INSERT INTO dieti_estates.address (latitude, longitude, city, country, province, street, street_number) VALUES (
    45.123456, 12.123456, 'Fictional City', 'Fantasyland', 'Fictional Province', 'Imaginary Street', '123'
);

INSERT INTO dieti_estates.agency (id_address, name) VALUES (
    1, 'The Hobbits'
);

-- Agent000@
-- 123Pass!
INSERT INTO dieti_estates.user (is_agent, is_manager, email, password, username, first_name, last_name)  VALUES
    (true, true, 'agent@gmail.com', '$2a$12$K5tOHLOh7nXYVuTukNf.cu0e.sf01a918YOWSjNYjBSUL0tWnfK4y', 'Agent01', 'Smith', 'Agente'),
    ( FALSE, FALSE, 'prova@gmail.com', '$2a$12$sSATIARk3Q51ZvMV1DsSIeLEXLyKlYKyWGKNk.ZURQwlGAjUMmEVu', 'User44', 'Fab', 'Apu');

INSERT INTO dieti_estates.property_category (category, subcategory, is_active) VALUES
    ('commercial property', 'commercial local', TRUE),
    ('commercial property', 'laboratory', TRUE),
    ('commercial property', 'commercial activity', TRUE),
    ('commercial property', 'storehouse', TRUE),
    ('commercial property', 'depot', TRUE),
    ('residential property', 'apartment', TRUE),
    ('residential property', 'penthouse', TRUE),
    ('residential property', 'attic', TRUE),
    ('residential property', 'loft', TRUE),
    ('residential property', 'detached house', TRUE),
    ('residential property', 'villa', TRUE),
    ('residential property', 'terraced house', TRUE),
    ('residential property', 'country house', TRUE),
    ('land', 'agricultural', TRUE),
    ('land', 'building', TRUE),
    ('land', 'non-building', TRUE),
    ('garage', 'garage', TRUE),
    ('garage', 'parking space', TRUE)
;

INSERT INTO dieti_estates.contract (name, is_active) VALUES (
    'rental', true
);

INSERT INTO dieti_estates.property (
    area, price, year_built, id_address, id_agent, id_contract, id_property_category, status, energy_rating, description) VALUES (
    150.0, 250000.00, 2015, 1, 1, 1, 1, 'UNDER_CONSTRUCTION', 'A1', 'Beautiful 3-bedroom house with modern amenities and a spacious garden.'
);

INSERT INTO dieti_estates.garage (id_property, has_surveillance, number_of_floors) VALUES (
    1, true, 1
);

INSERT INTO dieti_estates.commercial_property (id_property, floor, number_of_bathrooms, number_of_floors, number_of_rooms, numero_vetrine, wheelchair_access) VALUES (
    1, 0, 2, 1, 3, 1, true
);