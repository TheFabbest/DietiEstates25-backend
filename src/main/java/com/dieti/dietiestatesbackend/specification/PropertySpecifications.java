package com.dieti.dietiestatesbackend.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.data.jpa.domain.Specification;

import com.dieti.dietiestatesbackend.dto.request.FilterRequest;
import com.dieti.dietiestatesbackend.entities.CommercialProperty;
import com.dieti.dietiestatesbackend.entities.Garage;
import com.dieti.dietiestatesbackend.entities.Land;
import com.dieti.dietiestatesbackend.entities.Property;
import com.dieti.dietiestatesbackend.entities.ResidentialProperty;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Specifications per Property con:
 *  - Fetch mirato per prevenire N+1 (contract, propertyCategory, agent, address, heating)
 *  - Riduzione duplicazione di stringhe tramite costanti
 *  - Helper compatti e leggibili
 */
public final class PropertySpecifications {

    private PropertySpecifications() {}

    // Attributi comuni Property / relazioni
    private static final String DESCRIPTION = "description";
    private static final String CONTRACT = "contract";
    private static final String PROPERTY_CATEGORY = "propertyCategory";
    private static final String AGENT = "agent";
    private static final String ADDRESS = "address";
    private static final String HEATING = "heating";
    private static final String CATEGORY = "category";
    private static final String NAME = "name";
    private static final String PRICE = "price";
    private static final String AREA = "area";
    private static final String YEAR_BUILT = "yearBuilt";
    private static final String STATUS = "status";
    private static final String ENERGY_RATING = "energyRating";

    // Attributi subtype
    private static final String NUMBER_OF_ROOMS = "numberOfRooms";
    private static final String NUMBER_OF_BATHROOMS = "numberOfBathrooms";
    private static final String PARKING_SPACES = "parkingSpaces";
    private static final String GARDEN = "garden";
    private static final String IS_FURNISHED = "isFurnished";
    private static final String HAS_ELEVATOR = "hasElevator";
    private static final String HAS_WHEELCHAIR_ACCESS = "hasWheelchairAccess";
    private static final String NUMERO_VETRINE = "numeroVetrine";
    private static final String HAS_SURVEILLANCE = "hasSurveillance";
    private static final String ACCESSIBLE_FROM_STREET = "accessibleFromStreet";
    private static final String NUMBER_OF_FLOORS = "numberOfFloors";

    public static Specification<Property> withFilters(String keyword, FilterRequest filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            fetchCommonJoins(root, cb);
            addKeywordPredicate(keyword, root, cb, predicates);
            addCommonFilters(filters, root, cb, predicates);

            addResidentialFilters(filters, root, cb, predicates);
            addCommercialFilters(filters, root, cb, predicates);
            addGarageFilters(filters, root, cb, predicates);
            addLandFilters(filters, root, cb, predicates);
            addCommonPropertyTypeFloorsFilter(filters, root, cb, predicates);

            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // ---------- Fetch ----------
    private static void fetchCommonJoins(Root<Property> root, CriteriaBuilder cb) {
        root.fetch(CONTRACT, JoinType.LEFT);
        root.fetch(PROPERTY_CATEGORY, JoinType.LEFT);
        root.fetch(AGENT, JoinType.LEFT);
        root.fetch(ADDRESS, JoinType.LEFT);
        try {
            Root<ResidentialProperty> r = cb.treat(root, ResidentialProperty.class);
            r.fetch(HEATING, JoinType.LEFT);
        } catch (IllegalArgumentException ignored) {
            // non Residential => ignora
        }
    }

    // ---------- Keyword ----------
    private static void addKeywordPredicate(String keyword, Root<Property> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (keyword == null) return;
        String trimmed = keyword.trim();
        if (trimmed.isEmpty() || "all".equalsIgnoreCase(trimmed) || "filtered".equalsIgnoreCase(trimmed)) return;
        predicates.add(cb.like(cb.lower(root.get(DESCRIPTION)), "%" + trimmed.toLowerCase() + "%"));
    }

    // ---------- Common filters ----------
    private static void addCommonFilters(FilterRequest f, Root<Property> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (f.getCategory() != null) {
            predicates.add(cb.equal(cb.upper(root.get(PROPERTY_CATEGORY).get(CATEGORY)), f.getCategory().toUpperCase()));
        }
        if (f.getContract() != null) {
            predicates.add(cb.equal(cb.upper(root.get(CONTRACT).get(NAME)), f.getContract().toUpperCase()));
        }

        addIfNotNull(f.getMinPrice(), predicates, () -> cb.greaterThanOrEqualTo(root.get(PRICE), f.getMinPrice()));
        addIfNotNull(f.getMaxPrice(), predicates, () -> cb.lessThanOrEqualTo(root.get(PRICE), f.getMaxPrice()));
        addIfNotNull(f.getMinArea(), predicates, () -> cb.greaterThanOrEqualTo(root.get(AREA), f.getMinArea()));
        addIfNotNull(f.getMinYearBuilt(), predicates, () -> cb.greaterThanOrEqualTo(root.get(YEAR_BUILT), f.getMinYearBuilt()));

        if (f.getAcceptedStatus() != null && !f.getAcceptedStatus().isEmpty()) {
            predicates.add(root.get(STATUS).in(f.getAcceptedStatus()));
        }

        addIfNotNull(f.getMinEnergyRating(), predicates, () -> cb.greaterThanOrEqualTo(root.get(ENERGY_RATING), f.getMinEnergyRating()));
    }

    // ---------- Residential ----------
    private static void addResidentialFilters(FilterRequest f, Root<Property> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (!hasResidentialFilters(f)) return;

        Root<ResidentialProperty> r = cb.treat(root, ResidentialProperty.class);
        List<Predicate> list = new ArrayList<>();

        addIfNotNull(f.getMinNumberOfRooms(), list, () -> cb.greaterThanOrEqualTo(r.get(NUMBER_OF_ROOMS), f.getMinNumberOfRooms()));
        addIfNotNull(f.getMinNumberOfBathrooms(), list, () -> cb.greaterThanOrEqualTo(r.get(NUMBER_OF_BATHROOMS), f.getMinNumberOfBathrooms()));
        addIfNotNull(f.getMinParkingSpaces(), list, () -> cb.greaterThanOrEqualTo(r.get(PARKING_SPACES), f.getMinParkingSpaces()));

        if (f.getHeating() != null) {
            list.add(cb.equal(cb.upper(r.get(HEATING).get(NAME)), f.getHeating().toUpperCase()));
        }
        if (f.getAcceptedGarden() != null && !f.getAcceptedGarden().isEmpty()) {
            list.add(r.get(GARDEN).in(f.getAcceptedGarden()));
        }
        addIfNotNull(f.getMustBeFurnished(), list, () -> cb.equal(r.get(IS_FURNISHED), f.getMustBeFurnished()));
        addIfNotNull(f.getMustHaveElevator(), list, () -> cb.equal(r.get(HAS_ELEVATOR), f.getMustHaveElevator()));
        addIfNotNull(f.getMinNumberOfFloors(), list, () -> cb.greaterThanOrEqualTo(r.get(NUMBER_OF_FLOORS), f.getMinNumberOfFloors()));

        if (!list.isEmpty()) {
            predicates.add(cb.and(cb.equal(root.type(), ResidentialProperty.class), cb.and(list.toArray(new Predicate[0]))));
        }
    }

    // ---------- Commercial ----------
    private static void addCommercialFilters(FilterRequest f, Root<Property> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (!hasCommercialFilters(f)) return;

        Root<CommercialProperty> c = cb.treat(root, CommercialProperty.class);
        List<Predicate> list = new ArrayList<>();

        addIfNotNull(f.getMinNumberOfRooms(), list, () -> cb.greaterThanOrEqualTo(c.get(NUMBER_OF_ROOMS), f.getMinNumberOfRooms()));
        addIfNotNull(f.getMinNumberOfBathrooms(), list, () -> cb.greaterThanOrEqualTo(c.get(NUMBER_OF_BATHROOMS), f.getMinNumberOfBathrooms()));
        addIfNotNull(f.getMustHaveWheelchairAccess(), list, () -> cb.equal(c.get(HAS_WHEELCHAIR_ACCESS), f.getMustHaveWheelchairAccess()));
        addIfNotNull(f.getMinNumeroVetrine(), list, () -> cb.greaterThanOrEqualTo(c.get(NUMERO_VETRINE), f.getMinNumeroVetrine()));
        addIfNotNull(f.getMinNumberOfFloors(), list, () -> cb.greaterThanOrEqualTo(c.get(NUMBER_OF_FLOORS), f.getMinNumberOfFloors()));

        if (!list.isEmpty()) {
            predicates.add(cb.and(cb.equal(root.type(), CommercialProperty.class), cb.and(list.toArray(new Predicate[0]))));
        }
    }

    // ---------- Garage ----------
    private static void addGarageFilters(FilterRequest f, Root<Property> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (!hasGarageFilters(f)) return;

        Root<Garage> g = cb.treat(root, Garage.class);
        List<Predicate> list = new ArrayList<>();

        addIfNotNull(f.getMustHaveSurveillance(), list, () -> cb.equal(g.get(HAS_SURVEILLANCE), f.getMustHaveSurveillance()));
        addIfNotNull(f.getMinNumberOfFloors(), list, () -> cb.greaterThanOrEqualTo(g.get(NUMBER_OF_FLOORS), f.getMinNumberOfFloors()));

        if (!list.isEmpty()) {
            predicates.add(cb.and(cb.equal(root.type(), Garage.class), cb.and(list.toArray(new Predicate[0]))));
        }
    }

    // ---------- Land ----------
    private static void addLandFilters(FilterRequest f, Root<Property> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (!hasLandFilters(f)) return;

        Root<Land> l = cb.treat(root, Land.class);
        List<Predicate> list = new ArrayList<>();

        addIfNotNull(f.getMustBeAccessibleFromStreet(), list, () -> cb.equal(l.get(ACCESSIBLE_FROM_STREET), f.getMustBeAccessibleFromStreet()));

        if (!list.isEmpty()) {
            predicates.add(cb.and(cb.equal(root.type(), Land.class), cb.and(list.toArray(new Predicate[0]))));
        }
    }

    // ---------- Floors filter shared ----------
    private static void addCommonPropertyTypeFloorsFilter(FilterRequest f, Root<Property> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (f.getMinNumberOfFloors() == null) return;

        Root<ResidentialProperty> r = cb.treat(root, ResidentialProperty.class);
        Root<CommercialProperty> c = cb.treat(root, CommercialProperty.class);
        Root<Garage> g = cb.treat(root, Garage.class);

        predicates.add(cb.or(
            cb.and(cb.equal(root.type(), ResidentialProperty.class),
                   cb.greaterThanOrEqualTo(r.get(NUMBER_OF_FLOORS), f.getMinNumberOfFloors())),
            cb.and(cb.equal(root.type(), CommercialProperty.class),
                   cb.greaterThanOrEqualTo(c.get(NUMBER_OF_FLOORS), f.getMinNumberOfFloors())),
            cb.and(cb.equal(root.type(), Garage.class),
                   cb.greaterThanOrEqualTo(g.get(NUMBER_OF_FLOORS), f.getMinNumberOfFloors()))
        ));
    }

    // ---------- Utility ----------
    private static void addIfNotNull(Object value, List<Predicate> list, Supplier<Predicate> supplier) {
        if (value != null) list.add(supplier.get());
    }

    // ---------- hasX helpers ----------
    private static boolean hasResidentialFilters(FilterRequest f) {
        return f.getMinNumberOfRooms() != null ||
               f.getMinNumberOfBathrooms() != null ||
               f.getMinParkingSpaces() != null ||
               f.getHeating() != null ||
               (f.getAcceptedGarden() != null && !f.getAcceptedGarden().isEmpty()) ||
               f.getMustBeFurnished() != null ||
               f.getMustHaveElevator() != null ||
               f.getMinNumberOfFloors() != null;
    }

    private static boolean hasCommercialFilters(FilterRequest f) {
        return f.getMinNumberOfRooms() != null ||
               f.getMinNumberOfBathrooms() != null ||
               f.getMustHaveWheelchairAccess() != null ||
               f.getMinNumeroVetrine() != null ||
               f.getMinNumberOfFloors() != null;
    }

    private static boolean hasGarageFilters(FilterRequest f) {
        return f.getMustHaveSurveillance() != null ||
               f.getMinNumberOfFloors() != null;
    }

    private static boolean hasLandFilters(FilterRequest f) {
        return f.getMustBeAccessibleFromStreet() != null;
    }
}