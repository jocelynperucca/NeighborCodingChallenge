package org.example.neighborvehicle.models;

public class VehicleRequest {
    int quantity;
    int length;

    public int getLength() { return length;}
    public void setLength(int length) {
        this.length = length;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
