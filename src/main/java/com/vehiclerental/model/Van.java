package com.vehiclerental.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("VAN")
public class Van extends Vehicle {

    @Override
    public String getTypeCode() {
        return "VAN";
    }

    @Override
    public String getTypeName() {
        return "Van";
    }

    @Override
    public int getMaxSeats() {
        return 15;
    }

    @Override
    public String getDescription() {
        return "Spacious van for groups of up to " + getSeatingCapacity() + " - good for families and tours.";
    }
}
