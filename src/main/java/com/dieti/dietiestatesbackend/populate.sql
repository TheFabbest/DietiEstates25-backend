INSERT INTO dieti_estates.address (latitude, longitude, city, country, province, street, street_number) VALUES (
    45.123456, 12.123456, 'Fictional City', 'Fantasyland', 'Fictional Province', 'Imaginary Street', '123'
);

INSERT INTO dieti_estates.agenzia (id_address, nome) VALUES (
    1, 'The Hobbits'
);

INSERT INTO dieti_estates.autorimessa (ha_sorveglianza, numero_piani) VALUES (
    true, 1
);

INSERT INTO dieti_estates.property_category (category, subcategory, is_active) VALUES (
    'bella', 'molto bella', true
);

INSERT INTO dieti_estates.commercial_property (floor, number_of_bathrooms, number_of_floors, number_of_rooms, numero_vetrine, wheelchair_access) VALUES (
    0, 2, 1, 3, 1, true
);

INSERT INTO dieti_estates.contract (name, is_active) VALUES (
    'rental', true
);

INSERT INTO dieti_estates.garden (name, is_active) VALUES (
    'private', true
);

INSERT INTO dieti_estates.property (
    area, price, year_built, id_address, id_agent, id_contract, id_property_category, status, energy_rating, description) VALUES (
    150.0, 250000.00, 2015, 1, 1, 1, 1, 'under construction', 'A', 'Beautiful 3-bedroom house with modern amenities and a spacious garden.'
);