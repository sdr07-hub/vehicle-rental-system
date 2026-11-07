package com.vehiclerental.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CAR")
public class Car extends Vehicle {

    @Override
    public String getTypeCode() {
        return "CAR";
    }

    @Override
    public String getTypeName() {
        return "Car";
    }

    @Override
    public int getMaxSeats() {
        return 5;
    }

    @Override
    public String getDescription() {
        return "Comfortable car for up to " + getSeatingCapacity() + " passengers - best for city trips.";
    }
}
