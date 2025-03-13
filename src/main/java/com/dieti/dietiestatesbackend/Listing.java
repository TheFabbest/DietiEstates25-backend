package com.dieti.dietiestatesbackend;

class Listing {
    public final String name;
    public final String description;
    public final String location;
    public final float price;

    Listing(String name, String desc, String location, float price) {
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