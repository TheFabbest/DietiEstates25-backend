-- INSERT INTO dieti_estates.address (id, country, province, city, street, street_number, latitude, longitude, created_at) VALUES
-- (1, 'Italy', 'Rome', 'Rome', 'Via del Corso', '1', 41.902782, 12.483660, CURRENT_TIMESTAMP);

-- INSERT INTO dieti_estates.contract (id, name, is_active, created_at) VALUES
-- (1, 'Sale Contract', TRUE, CURRENT_TIMESTAMP);

-- INSERT INTO dieti_estates.heating (id, name, is_active, created_at) VALUES
-- (1, 'Centralized', TRUE, CURRENT_TIMESTAMP);

-- INSERT INTO dieti_estates.property_category (id, property_type, name, is_active, created_at) VALUES
-- (1, 'RESIDENTIAL', 'Apartment', true, CURRENT_TIMESTAMP),
-- (2, 'COMMERCIAL', 'Office', true, CURRENT_TIMESTAMP),
-- (3, 'LAND', 'Building Plot', true, CURRENT_TIMESTAMP),
-- (4, 'GARAGE', 'Single Garage', true, CURRENT_TIMESTAMP);

-- INSERT INTO dieti_estates.agency (id, name, id_address, created_at) VALUES
-- (1, 'DietiEstates Agency', 1, CURRENT_TIMESTAMP);

INSERT INTO dieti_estates.residential_property (id, number_of_rooms, number_of_bathrooms, parking_spaces, id_heating, garden, is_furnished, floor, number_of_floors, has_elevator) VALUES
(1, 3, 2, 1, 1, 'PRIVATE', TRUE, 3, 5, TRUE);

-- INSERT INTO dieti_estates.property (id, id_address, id_contract, id_property_category, id_agent, description, price, area, year_built, condition, energy_rating, additional_features, image_directory_ulid, number_of_images, property_type, created_at, updated_at) VALUES
-- (2, 1, 1, 2, 1, 'Modern office space', 500000.00, 200, 2010, 'GOOD_CONDITION', 'B', 'Meeting rooms, high-speed internet', '01ARZ3NDEKTSV4RRFFQ69G52XR', 3, 'COMMERCIAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- INSERT INTO dieti_estates.commercial_property (id, number_of_rooms, floor, number_of_bathrooms, number_of_floors, wheelchair_access) VALUES
-- (2, 5, 2, 2, 10, TRUE);

-- INSERT INTO dieti_estates.property (id, id_address, id_contract, id_property_category, id_agent, description, price, area, year_built, condition, energy_rating, additional_features, image_directory_ulid, number_of_images, property_type, created_at, updated_at) VALUES
-- (3, 1, 1, 3, 1, 'Spacious building plot', 150000.00, 500, NULL, 'GOOD_CONDITION', 'NOT_APPLICABLE', 'Ready for construction', '01ARZ3NDEKTSV4RRFFQ69G52XS', 0, 'LAND', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- INSERT INTO dieti_estates.land (id, accessible_from_street) VALUES
-- (3, TRUE);

-- INSERT INTO dieti_estates.property (id, id_address, id_contract, id_property_category, id_agent, description, price, area, year_built, condition, energy_rating, additional_features, image_directory_ulid, number_of_images, property_type, created_at, updated_at) VALUES
-- (4, 1, 1, 4, 1, 'Single garage for rent', 20000.00, 20, 1995, 'GOOD_CONDITION', 'NOT_APPLICABLE', 'Secure, automatic door', '01ARZ3NDEKTSV4RRFFQ69G52XT', 1, 'GARAGE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- INSERT INTO dieti_estates.garage (id, has_surveillance, floor, number_of_floors) VALUES
-- (4, FALSE, 0, 1);

-- INSERT INTO dieti_estates.agent_availability (id, id_agent, start_time, end_time, created_at) VALUES
-- (1, 1, '2025-11-01 09:00:00', '2025-11-01 17:00:00', CURRENT_TIMESTAMP);

-- INSERT INTO dieti_estates.offer (id, id_property, id_user, price, status, created_at) VALUES
-- (1, 1, 3, 240000.00, 'PENDING', CURRENT_TIMESTAMP);

-- INSERT INTO dieti_estates.refresh_token (id, token_value, user_id, expiry_date, created_at) VALUES
-- (1, 'some_refresh_token_value', 3, '2025-12-31 23:59:59', CURRENT_TIMESTAMP);

-- INSERT INTO dieti_estates.visit (id, id_property, id_user, id_agent, start_time, end_time, status, created_at) VALUES
-- (1, 1, 3, 1, '2025-11-02 10:00:00', '2025-11-02 11:00:00', 'PENDING', CURRENT_TIMESTAMP);