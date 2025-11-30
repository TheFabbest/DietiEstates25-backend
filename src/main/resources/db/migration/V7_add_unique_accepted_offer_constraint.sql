CREATE UNIQUE INDEX uk_offer_property_accepted 
ON offer (id_property) 
WHERE status = 'ACCEPTED';