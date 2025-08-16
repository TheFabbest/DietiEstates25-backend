package com.dieti.dietiestatesbackend.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.dieti.dietiestatesbackend.dto.request.FilterRequest;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.ResidentialProperty;
import com.dieti.dietiestatesbackend.entities.CommercialProperty;
import com.dieti.dietiestatesbackend.entities.Garage;
import com.dieti.dietiestatesbackend.entities.Land;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class PropertySpecifications {

    public static Specification<Property> withFilters(String keyword, FilterRequest filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Keyword search
            if (keyword != null && !keyword.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")),
                    "%" + keyword.toLowerCase() + "%"
                ));
            }

            // Common filters
            if (filters.getCategory() != null) {
                predicates.add(criteriaBuilder.equal(
                    criteriaBuilder.upper(root.get("propertyCategory").get("category")),
                    filters.getCategory().toUpperCase()
                ));
            }

            if (filters.getContract() != null) {
                predicates.add(criteriaBuilder.equal(
                    criteriaBuilder.upper(root.get("contract").get("name")),
                    filters.getContract().toUpperCase()
                ));
            }

            if (filters.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), filters.getMinPrice()));
            }

            if (filters.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), filters.getMaxPrice()));
            }

            if (filters.getMinArea() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("area"), filters.getMinArea()));
            }

            if (filters.getMinYearBuilt() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("yearBuilt"), filters.getMinYearBuilt()));
            }

            if (filters.getAcceptedStatus() != null && !filters.getAcceptedStatus().isEmpty()) {
                predicates.add(root.get("status").in(filters.getAcceptedStatus()));
            }

            if (filters.getMinEnergyRating() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("energyRating"), filters.getMinEnergyRating()));
            }

            // Property-specific filters
            addResidentialFilters(filters, root, criteriaBuilder, predicates);
            addCommercialFilters(filters, root, criteriaBuilder, predicates);
            addGarageFilters(filters, root, criteriaBuilder, predicates);
            addLandFilters(filters, root, criteriaBuilder, predicates);
            addCommonPropertyTypeFilters(filters, root, criteriaBuilder, predicates);

            query.distinct(true);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addResidentialFilters(FilterRequest filters, 
            jakarta.persistence.criteria.Root<Property> root,
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder,
            List<Predicate> predicates) {
        
        if (hasResidentialFilters(filters)) {
            Join<Property, ResidentialProperty> residentialJoin = root.join("residentialProperty", JoinType.LEFT);
            
            List<Predicate> residentialPredicates = new ArrayList<>();
            
            if (filters.getMinNumberOfRooms() != null) {
                residentialPredicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    residentialJoin.get("numberOfRooms"), filters.getMinNumberOfRooms()));
            }
            
            if (filters.getMinNumberOfBathrooms() != null) {
                residentialPredicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    residentialJoin.get("numberOfBathrooms"), filters.getMinNumberOfBathrooms()));
            }
            
            if (filters.getMinParkingSpaces() != null) {
                residentialPredicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    residentialJoin.get("parkingSpaces"), filters.getMinParkingSpaces()));
            }
            
            if (filters.getHeating() != null) {
                residentialPredicates.add(criteriaBuilder.equal(
                    criteriaBuilder.upper(residentialJoin.get("heating").get("name")),
                    filters.getHeating().toUpperCase()));
            }
            
            if (filters.getAcceptedGarden() != null && !filters.getAcceptedGarden().isEmpty()) {
                residentialPredicates.add(residentialJoin.get("garden").in(filters.getAcceptedGarden()));
            }
            
            if (filters.getMustBeFurnished() != null) {
                residentialPredicates.add(criteriaBuilder.equal(
                    residentialJoin.get("isFurnished"), filters.getMustBeFurnished()));
            }
            
            if (filters.getMustHaveElevator() != null) {
                residentialPredicates.add(criteriaBuilder.equal(
                    residentialJoin.get("hasElevator"), filters.getMustHaveElevator()));
            }
            
            if (filters.getMinNumberOfFloors() != null) {
                residentialPredicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    residentialJoin.get("numberOfFloors"), filters.getMinNumberOfFloors()));
            }
            
            if (!residentialPredicates.isEmpty()) {
                predicates.add(criteriaBuilder.and(
                    criteriaBuilder.isNotNull(residentialJoin.get("id")),
                    criteriaBuilder.and(residentialPredicates.toArray(new Predicate[0]))
                ));
            }
        }
    }

    private static void addCommercialFilters(FilterRequest filters,
            jakarta.persistence.criteria.Root<Property> root,
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder,
            List<Predicate> predicates) {
        
        if (hasCommercialFilters(filters)) {
            Join<Property, CommercialProperty> commercialJoin = root.join("commercialProperty", JoinType.LEFT);
            
            List<Predicate> commercialPredicates = new ArrayList<>();
            
            if (filters.getMinNumberOfRooms() != null) {
                commercialPredicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    commercialJoin.get("numberOfRooms"), filters.getMinNumberOfRooms()));
            }
            
            if (filters.getMinNumberOfBathrooms() != null) {
                commercialPredicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    commercialJoin.get("numberOfBathrooms"), filters.getMinNumberOfBathrooms()));
            }
            
            if (filters.getMustHaveWheelchairAccess() != null) {
                commercialPredicates.add(criteriaBuilder.equal(
                    commercialJoin.get("hasWheelchairAccess"), filters.getMustHaveWheelchairAccess()));
            }
            
            if (filters.getMinNumeroVetrine() != null) {
                commercialPredicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    commercialJoin.get("numeroVetrine"), filters.getMinNumeroVetrine()));
            }
            
            if (filters.getMinNumberOfFloors() != null) {
                commercialPredicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    commercialJoin.get("numberOfFloors"), filters.getMinNumberOfFloors()));
            }
            
            if (!commercialPredicates.isEmpty()) {
                predicates.add(criteriaBuilder.and(
                    criteriaBuilder.isNotNull(commercialJoin.get("id")),
                    criteriaBuilder.and(commercialPredicates.toArray(new Predicate[0]))
                ));
            }
        }
    }

    private static void addGarageFilters(FilterRequest filters,
            jakarta.persistence.criteria.Root<Property> root,
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder,
            List<Predicate> predicates) {
        
        if (hasGarageFilters(filters)) {
            Join<Property, Garage> garageJoin = root.join("garage", JoinType.LEFT);
            
            List<Predicate> garagePredicates = new ArrayList<>();
            
            if (filters.getMustHaveSurveillance() != null) {
                garagePredicates.add(criteriaBuilder.equal(
                    garageJoin.get("hasSurveillance"), filters.getMustHaveSurveillance()));
            }
            
            if (filters.getMinNumberOfFloors() != null) {
                garagePredicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    garageJoin.get("numberOfFloors"), filters.getMinNumberOfFloors()));
            }
            
            if (!garagePredicates.isEmpty()) {
                predicates.add(criteriaBuilder.and(
                    criteriaBuilder.isNotNull(garageJoin.get("id")),
                    criteriaBuilder.and(garagePredicates.toArray(new Predicate[0]))
                ));
            }
        }
    }

    private static void addLandFilters(FilterRequest filters,
            jakarta.persistence.criteria.Root<Property> root,
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder,
            List<Predicate> predicates) {
        
        if (hasLandFilters(filters)) {
            Join<Property, Land> landJoin = root.join("land", JoinType.LEFT);
            
            List<Predicate> landPredicates = new ArrayList<>();
            
            if (filters.getMustBeAccessibleFromStreet() != null) {
                landPredicates.add(criteriaBuilder.equal(
                    landJoin.get("accessibleFromStreet"), filters.getMustBeAccessibleFromStreet()));
            }
            
            if (!landPredicates.isEmpty()) {
                predicates.add(criteriaBuilder.and(
                    criteriaBuilder.isNotNull(landJoin.get("id")),
                    criteriaBuilder.and(landPredicates.toArray(new Predicate[0]))
                ));
            }
        }
    }

    private static void addCommonPropertyTypeFilters(FilterRequest filters,
            jakarta.persistence.criteria.Root<Property> root,
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder,
            List<Predicate> predicates) {
        
        // Handle filters that apply to multiple property types
        if (filters.getMinNumberOfFloors() != null) {
            Join<Property, ResidentialProperty> residentialJoin = root.join("residentialProperty", JoinType.LEFT);
            Join<Property, CommercialProperty> commercialJoin = root.join("commercialProperty", JoinType.LEFT);
            Join<Property, Garage> garageJoin = root.join("garage", JoinType.LEFT);
            
            predicates.add(criteriaBuilder.or(
                criteriaBuilder.and(
                    criteriaBuilder.isNotNull(residentialJoin.get("id")),
                    criteriaBuilder.greaterThanOrEqualTo(residentialJoin.get("numberOfFloors"), filters.getMinNumberOfFloors())
                ),
                criteriaBuilder.and(
                    criteriaBuilder.isNotNull(commercialJoin.get("id")),
                    criteriaBuilder.greaterThanOrEqualTo(commercialJoin.get("numberOfFloors"), filters.getMinNumberOfFloors())
                ),
                criteriaBuilder.and(
                    criteriaBuilder.isNotNull(garageJoin.get("id")),
                    criteriaBuilder.greaterThanOrEqualTo(garageJoin.get("numberOfFloors"), filters.getMinNumberOfFloors())
                )
            ));
        }
    }

    // Helper methods to check if specific filters are applied
    private static boolean hasResidentialFilters(FilterRequest filters) {
        return filters.getMinParkingSpaces() != null || 
               filters.getHeating() != null ||
               (filters.getAcceptedGarden() != null && !filters.getAcceptedGarden().isEmpty()) ||
               filters.getMustBeFurnished() != null ||
               filters.getMustHaveElevator() != null;
    }

    private static boolean hasCommercialFilters(FilterRequest filters) {
        return filters.getMustHaveWheelchairAccess() != null ||
               filters.getMinNumeroVetrine() != null;
    }

    private static boolean hasGarageFilters(FilterRequest filters) {
        return filters.getMustHaveSurveillance() != null;
    }

    private static boolean hasLandFilters(FilterRequest filters) {
        return filters.getMustBeAccessibleFromStreet() != null;
    }
}