package com.dieti.dietiestatesbackend.dto;

public class Listing {
    public final long id;
    public final String name;
    public final String description;
    public final String location;
    public final float price;

    public Listing(long id, String name, String desc, String location, float price) {
        this.id = id;
        this.name = name;
        description = desc;
        this.location = location;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public float getPrice() {
        return price;
    }
}