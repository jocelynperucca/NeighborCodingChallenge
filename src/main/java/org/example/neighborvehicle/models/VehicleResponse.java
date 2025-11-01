package org.example.neighborvehicle.models;

import java.util.List;

public class VehicleResponse {
    private String location_id;
    private List<String> listing_ids;
    private int total_price_in_cents;


    public VehicleResponse(String location_id, List<String> listing_ids, int total_price_in_cents) {
        this.location_id = location_id;
        this.listing_ids = listing_ids;
        this.total_price_in_cents = total_price_in_cents;
    }


    public String getLocation_id() { return location_id; }
    public void setLocation_id(String location_id) { this.location_id = location_id; }

    public List<String> getListing_ids() { return listing_ids; }
    public void setListing_ids(List<String> listing_ids) { this.listing_ids = listing_ids; }

    public int getTotal_price_in_cents() { return total_price_in_cents; }
    public void setTotal_price_in_cents(int total_price_in_cents) { this.total_price_in_cents = total_price_in_cents; }

    @Override
    public String toString() {
        return "Location: " + location_id +
                " | Listings: " + listing_ids +
                " | Total Price (¢): " + total_price_in_cents;
    }
}
