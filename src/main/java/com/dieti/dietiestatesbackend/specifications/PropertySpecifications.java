package com.dieti.dietiestatesbackend.specifications;

import com.dieti.dietiestatesbackend.dto.request.FilterRequest;
import com.dieti.dietiestatesbackend.entities.Address;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.Contract;
import com.dieti.dietiestatesbackend.entities.PropertyCategory;
import com.dieti.dietiestatesbackend.entities.ResidentialProperty;
import com.dieti.dietiestatesbackend.entities.CommercialProperty;
import com.dieti.dietiestatesbackend.entities.Garage;
import com.dieti.dietiestatesbackend.entities.Land;
import com.dieti.dietiestatesbackend.entities.Heating;
import com.dieti.dietiestatesbackend.enums.EnergyRating;
import com.dieti.dietiestatesbackend.enums.PropertyCondition;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import com.dieti.dietiestatesbackend.util.BoundingBoxUtility;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class PropertySpecifications {

    public static Specification<Property> buildFromFilters(FilterRequest filters) {
        Specification<Property> spec = (root, query, cb) -> cb.conjunction();

        spec = spec.and(priceInRange(filters.getMinPrice(), filters.getMaxPrice()));
        spec = spec.and(minArea(filters.getMinArea()));
        spec = spec.and(minYearBuilt(filters.getMinYearBuilt()));
        spec = spec.and(conditionIn(filters.getAcceptedCondition()));
        spec = spec.and(minEnergyRating(filters.getMinEnergyRating()));
        spec = spec.and(contractEquals(filters.getContract()));
        spec = spec.and(categoryEquals(filters.getCategory()));
        spec = spec.and(subcategoryEquals(filters.getPropertySubcategoryName()));
        spec = spec.and(typeSpecificFilters(filters));
        spec = spec.and(withinBoundingBox(filters)); // Filtro geografico basato sulla bounding box

        return spec;
    }

    private static Specification<Property> priceInRange(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (min != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), min));
            }
            if (max != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), max));
            }
            return combinePredicates(cb, predicates);
        };
    }

    private static Specification<Property> minArea(Integer minArea) {
        return (root, query, cb) -> minArea == null ? null : cb.greaterThanOrEqualTo(root.get("area"), minArea);
    }

    private static Specification<Property> minYearBuilt(Integer year) {
        return (root, query, cb) -> year == null ? null : cb.greaterThanOrEqualTo(root.get("yearBuilt"), year);
    }

    private static Specification<Property> conditionIn(List<PropertyCondition> conditions) {
        return (root, query, cb) -> {
            if (isNullOrEmpty(conditions)) {
                return null;
            }
            return root.get("condition").in(conditions);
        };
    }

    private static Specification<Property> minEnergyRating(EnergyRating rating) {
        return (root, query, cb) -> rating == null ? null : cb.greaterThanOrEqualTo(root.get("energyRating"), rating);
    }

    private static Specification<Property> contractEquals(String contractName) {
        return (root, query, cb) -> {
            if (isNullOrBlank(contractName)) {
                return null;
            }
            Join<Property, Contract> contractJoin = root.join("contract");
            return cb.equal(contractJoin.get("name"), contractName.trim());
        };
    }

    private static Specification<Property> categoryEquals(String propertyType) {
        return (root, query, cb) -> {
            if (isNullOrBlank(propertyType)) {
                return null;
            }
            Join<Property, PropertyCategory> categoryJoin = root.join("propertyCategory");
            return cb.equal(categoryJoin.get("propertyType"), propertyType.trim());
        };
    }

    private static Specification<Property> subcategoryEquals(String subcategoryName) {
        return (root, query, cb) -> {
            if (isNullOrBlank(subcategoryName)) {
                return null;
            }
            Join<Property, PropertyCategory> categoryJoin = root.join("propertyCategory");
            return cb.equal(categoryJoin.get("name"), subcategoryName.trim());
        };
    }

    private static Specification<Property> typeSpecificFilters(FilterRequest filters) {
        return (root, query, cb) -> {
            String rawCategory = safeTrim(filters.getCategory());
            if (rawCategory == null) {
                return null;
            }

            List<Predicate> predicates = new ArrayList<>();
            String category = rawCategory.toUpperCase();

            switch (category) {
                case "RESIDENTIAL":
                    addPredicateIfNotNull(predicates, residentialFilters(filters).toPredicate(root, query, cb));
                    break;
                case "COMMERCIAL":
                    addPredicateIfNotNull(predicates, commercialFilters(filters).toPredicate(root, query, cb));
                    break;
                case "GARAGE":
                    addPredicateIfNotNull(predicates, garageFilters(filters).toPredicate(root, query, cb));
                    break;
                case "LAND":
                    addPredicateIfNotNull(predicates, landFilters(filters).toPredicate(root, query, cb));
                    break;
                default:
                    // unknown category -> no type-specific predicates
                    break;
            }

            return combinePredicates(cb, predicates);
        };
    }

    private static Specification<Property> commercialFilters(FilterRequest filters) {
        return (root, query, cb) -> {
            Root<CommercialProperty> commercialRoot = cb.treat(root, CommercialProperty.class);
            List<Predicate> predicates = new ArrayList<>();

            if (filters.getMinNumberOfFloors() != null) {
                predicates.add(cb.greaterThanOrEqualTo(commercialRoot.get("numberOfFloors"), filters.getMinNumberOfFloors()));
            }
            if (filters.getMustHaveWheelchairAccess() != null) {
                predicates.add(cb.equal(commercialRoot.get("hasWheelchairAccess"), filters.getMustHaveWheelchairAccess()));
            }

            return combinePredicates(cb, predicates);
        };
    }

    private static Specification<Property> residentialFilters(FilterRequest filters) {
        return (root, query, cb) -> {
            Root<ResidentialProperty> residentialRoot = cb.treat(root, ResidentialProperty.class);
            List<Predicate> predicates = new ArrayList<>();

            if (filters.getMinNumberOfRooms() != null) {
                predicates.add(cb.greaterThanOrEqualTo(residentialRoot.get("numberOfRooms"), filters.getMinNumberOfRooms()));
            }
            if (filters.getMinNumberOfBathrooms() != null) {
                predicates.add(cb.greaterThanOrEqualTo(residentialRoot.get("numberOfBathrooms"), filters.getMinNumberOfBathrooms()));
            }
            if (filters.getMinParkingSpaces() != null) {
                predicates.add(cb.greaterThanOrEqualTo(residentialRoot.get("parkingSpaces"), filters.getMinParkingSpaces()));
            }
            if (!isNullOrBlank(filters.getHeating())) {
                Join<ResidentialProperty, Heating> heatingJoin = residentialRoot.join("heating");
                predicates.add(cb.equal(heatingJoin.get("name"), filters.getHeating().trim()));
            }
            if (!isNullOrEmpty(filters.getAcceptedGarden())) {
                predicates.add(residentialRoot.get("garden").in(filters.getAcceptedGarden()));
            }
            if (filters.getMustBeFurnished() != null) {
                predicates.add(cb.equal(residentialRoot.get("isFurnished"), filters.getMustBeFurnished()));
            }
            if (filters.getMustHaveElevator() != null) {
                predicates.add(cb.equal(residentialRoot.get("hasElevator"), filters.getMustHaveElevator()));
            }

            return combinePredicates(cb, predicates);
        };
    }

    private static Specification<Property> garageFilters(FilterRequest filters) {
        return (root, query, cb) -> {
            Root<Garage> garageRoot = cb.treat(root, Garage.class);
            List<Predicate> predicates = new ArrayList<>();

            if (filters.getMustHaveSurveillance() != null) {
                predicates.add(cb.equal(garageRoot.get("hasSurveillance"), filters.getMustHaveSurveillance()));
            }
            if (filters.getMinNumberOfFloors() != null) {
                predicates.add(cb.greaterThanOrEqualTo(garageRoot.get("numberOfFloors"), filters.getMinNumberOfFloors()));
            }

            return combinePredicates(cb, predicates);
        };
    }

    private static Specification<Property> landFilters(FilterRequest filters) {
        return (root, query, cb) -> {
            Root<Land> landRoot = cb.treat(root, Land.class);
            List<Predicate> predicates = new ArrayList<>();

            if (filters.getMustBeAccessibleFromStreet() != null) {
                predicates.add(cb.equal(landRoot.get("accessibleFromStreet"), filters.getMustBeAccessibleFromStreet()));
            }

            return combinePredicates(cb, predicates);
        };
    }

    private static Specification<Property> withinBoundingBox(FilterRequest filters) {
        return (root, query, cb) -> {
            if (filters == null ||
                filters.getCenterLatitude() == null ||
                filters.getCenterLongitude() == null ||
                filters.getRadiusInMeters() == null) {
                return null;
            }

            // Calcola i limiti della bounding box usando la utility (istanziata direttamente:
            // la classe è un componente senza dipendenze quindi è sicuro crearne un'istanza qui).
            BoundingBoxUtility util = new BoundingBoxUtility();
            java.math.BigDecimal[] bounds = util.calculateBoundingBox(
                filters.getCenterLatitude(),
                filters.getCenterLongitude(),
                filters.getRadiusInMeters()
            );

            java.math.BigDecimal minLat = bounds[0];
            java.math.BigDecimal maxLat = bounds[1];
            java.math.BigDecimal minLon = bounds[2];
            java.math.BigDecimal maxLon = bounds[3];

            Join<Property, Address> addressJoin = root.join("address");

            jakarta.persistence.criteria.Path<java.math.BigDecimal> latPath = addressJoin.get("coordinates").get("latitude");
            jakarta.persistence.criteria.Path<java.math.BigDecimal> lonPath = addressJoin.get("coordinates").get("longitude");

            Predicate latPredicate = cb.and(
                cb.greaterThanOrEqualTo(latPath, minLat),
                cb.lessThanOrEqualTo(latPath, maxLat)
            );

            Predicate lonPredicate;
            // Se la bounding box attraversa l'antimeridiano, minLon > maxLon -> OR fra i due intervalli
            if (minLon.compareTo(maxLon) <= 0) {
                lonPredicate = cb.and(
                    cb.greaterThanOrEqualTo(lonPath, minLon),
                    cb.lessThanOrEqualTo(lonPath, maxLon)
                );
            } else {
                lonPredicate = cb.or(
                    cb.greaterThanOrEqualTo(lonPath, minLon),
                    cb.lessThanOrEqualTo(lonPath, maxLon)
                );
            }

            return cb.and(latPredicate, lonPredicate);
        };
    }

    /* -------------------------
       Helper methods - keep predicates clean and checks centralized
       ------------------------- */

    private static Predicate combinePredicates(jakarta.persistence.criteria.CriteriaBuilder cb, List<Predicate> predicates) {
        if (predicates == null || predicates.isEmpty()) {
            return null;
        }
        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private static void addPredicateIfNotNull(List<Predicate> list, Predicate p) {
        if (p != null) {
            list.add(p);
        }
    }

    private static boolean isNullOrBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static boolean isNullOrEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    private static String safeTrim(String s) {
        return isNullOrBlank(s) ? null : s.trim();
    }
}