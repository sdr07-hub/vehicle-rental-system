package com.vehiclerental.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SUV")
public class Suv extends Vehicle {

    @Override
    public String getTypeCode() {
        return "SUV";
    }

    @Override
    public String getTypeName() {
        return "SUV";
    }

    @Override
    public int getMaxSeats() {
        return 7;
    }

    @Override
    public String getDescription() {
        return "Powerful SUV for up to " + getSeatingCapacity()
                + " passengers - ideal for hill country and long trips.";
    }
}
