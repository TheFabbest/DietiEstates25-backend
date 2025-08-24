package com.dieti.dietiestatesbackend.entities;

/**
 * Visitor interface for Property hierarchy to avoid instanceof checks in mappers.
 */
public interface PropertyVisitor {
    void visit(ResidentialProperty residentialProperty);
    void visit(CommercialProperty commercialProperty);
    void visit(Garage garage);
    void visit(Land land);
    void visit(Property property); // fallback for generic properties
}