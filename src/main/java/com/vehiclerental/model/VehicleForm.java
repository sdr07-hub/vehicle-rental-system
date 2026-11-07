package com.vehiclerental.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Concrete form data for binding input before the factory chooses a Vehicle subtype. */
public class VehicleForm {

    @NotBlank(message = "Please choose a vehicle type")
    private String vehicleType;

    @NotBlank(message = "Registration number is required")
    @Pattern(regexp = "^[A-Za-z]{2,3}-[0-9]{4}$", message = "Use the format ABC-1234 or AB-1234")
    private String registrationNumber;

    @NotBlank(message = "Brand is required")
    @Size(max = 50, message = "Brand must be at most 50 characters")
    private String brand;

    @NotBlank(message = "Model is required")
    @Size(max = 50, message = "Model must be at most 50 characters")
    private String model;

    @NotNull(message = "Manufacture year is required")
    @Min(value = 1990, message = "Manufacture year must be 1990 or later")
    private Integer manufactureYear;

    @NotNull(message = "Seating capacity is required")
    @Min(value = 1, message = "Seating capacity must be at least 1")
    @Max(value = 50, message = "Seating capacity must be at most 50")
    private Integer seatingCapacity;

    @Size(max = 30, message = "Colour must be at most 30 characters")
    private String colour;

    @NotNull(message = "Please choose a status")
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    public static VehicleForm fromVehicle(Vehicle vehicle) {
        VehicleForm form = new VehicleForm();
        form.setVehicleType(vehicle.getTypeCode());
        form.setRegistrationNumber(vehicle.getRegistrationNumber());
        form.setBrand(vehicle.getBrand());
        form.setModel(vehicle.getModel());
        form.setManufactureYear(vehicle.getManufactureYear());
        form.setSeatingCapacity(vehicle.getSeatingCapacity());
        form.setColour(vehicle.getColour());
        form.setStatus(vehicle.getStatus());
        return form;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber == null ? null : registrationNumber.trim();
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getManufactureYear() {
        return manufactureYear;
    }

    public void setManufactureYear(Integer manufactureYear) {
        this.manufactureYear = manufactureYear;
    }

    public Integer getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(Integer seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }
}
