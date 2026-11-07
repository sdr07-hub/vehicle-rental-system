package com.vehiclerental.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Price settings for one vehicle type (CAR, VAN or SUV).
 * Fare = base fare + (price per km x distance), but never less than the minimum fare.
 *
 * OOP: Inheritance (extends BaseEntity), Abstraction + Polymorphism (implements FareCalculator),
 *      Encapsulation (the fare formula is hidden inside calculateFare()).
 */
@Entity
@Table(name = "fare_rules")
public class FareRule extends BaseEntity implements FareCalculator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fare_rule_id")
    private Long fareRuleId;

    @NotBlank(message = "Please choose a vehicle type")
    @Pattern(regexp = "^(CAR|VAN|SUV)$", message = "Vehicle type must be CAR, VAN or SUV")
    @Column(name = "vehicle_type", nullable = false, unique = true, length = 10)
    private String vehicleType;

    @NotNull(message = "Base fare is required")
    @DecimalMin(value = "0.00", message = "Base fare cannot be negative")
    @Column(name = "base_fare", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseFare;

    @NotNull(message = "Price per km is required")
    @DecimalMin(value = "0.01", message = "Price per km must be more than 0")
    @Column(name = "price_per_km", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerKm;

    @NotNull(message = "Minimum fare is required")
    @DecimalMin(value = "0.00", message = "Minimum fare cannot be negative")
    @Column(name = "minimum_fare", nullable = false, precision = 10, scale = 2)
    private BigDecimal minimumFare;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Override
    public BigDecimal calculateFare(BigDecimal distanceKm) {
        BigDecimal fare = baseFare.add(pricePerKm.multiply(distanceKm));
        if (fare.compareTo(minimumFare) < 0) {
            fare = minimumFare;
        }
        return fare.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getCalculatorName() {
        return "Standard " + vehicleType + " fare";
    }

    public Long getFareRuleId() {
        return fareRuleId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public BigDecimal getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(BigDecimal baseFare) {
        this.baseFare = baseFare;
    }

    public BigDecimal getPricePerKm() {
        return pricePerKm;
    }

    public void setPricePerKm(BigDecimal pricePerKm) {
        this.pricePerKm = pricePerKm;
    }

    public BigDecimal getMinimumFare() {
        return minimumFare;
    }

    public void setMinimumFare(BigDecimal minimumFare) {
        this.minimumFare = minimumFare;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "FareRule{" + vehicleType + ": base=" + baseFare + ", perKm=" + pricePerKm + ", min=" + minimumFare + "}";
    }
}
