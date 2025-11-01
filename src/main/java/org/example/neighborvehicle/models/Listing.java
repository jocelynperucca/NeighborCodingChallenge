package org.example.neighborvehicle.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Listing {
    private String id;
    @JsonProperty("location_id")
    private String location_id;
    private int length;
    private int width;
    @JsonProperty("price_in_cents")
    private int price;

    public String getId() { return id;}
    public void setId(String id) {
        this.id = id;
    }

    public int getLength() { return length;}
    public void setLength(int length) { this.length = length;}

    public String getLocationId() { return location_id;}
    public void setLocationId(String location_id) {
        this.location_id = location_id;
    }

    public int getWidth() { return width;}
    public void setWidth(int width) {
        this.width = width;
    }

    public int getPrice() { return price;}
    public void setPrice(int price_in_cents) {
        this.price = price_in_cents;
    }

}
