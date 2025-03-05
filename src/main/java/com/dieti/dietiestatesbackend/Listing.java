package com.dieti.dietiestatesbackend;

class Listing {
  private final String name;
  private final String description;
  private final String location;
  private final float price;

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