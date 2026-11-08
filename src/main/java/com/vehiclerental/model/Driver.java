package com.vehiclerental.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * A driver who is assigned to bookings.
 * Common details come from User (users table); licence and availability are saved in the drivers table.
 *
 * OOP: Inheritance (extends User), Encapsulation (private fields + rules inside methods),
 *      Polymorphism (overrides getRoleLabel and getProfileSummary).
 */
@Entity
@Table(name = "drivers")
@PrimaryKeyJoinColumn(name = "driver_id")
public class Driver extends User {

    @NotBlank(message = "Licence number is required")
    @Pattern(regexp = "^[A-Za-z0-9]{5,15}$", message = "Licence number must be 5-15 letters or numbers")
    @Column(name = "licence_number", nullable = false, unique = true, length = 30)
    private String licenceNumber;

    @NotNull(message = "Licence expiry date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "licence_expiry", nullable = false)
    private LocalDate licenceExpiry;

    @Min(value = 0, message = "Experience cannot be negative")
    @Max(value = 60, message = "Experience must be 60 years or less")
    @Column(name = "years_of_experience", nullable = false)
    private int yearsOfExperience;

    @NotNull(message = "Please choose the availability")
    @Enumerated(EnumType.STRING)
    @Column(name = "availability", nullable = false, length = 20)
    private DriverAvailability availability = DriverAvailability.AVAILABLE;

    public Driver() {
        setRole(UserRole.DRIVER);
    }

    // ----- Business rules kept inside the class (Encapsulation) -----

    /** A licence is valid if it has not expired yet. */
    public boolean isLicenceValid() {
        return licenceExpiry != null && !licenceExpiry.isBefore(LocalDate.now());
    }

    /** "Valid", "Expiring soon" (within 30 days) or "Expired". */
    public String getLicenceStatus() {
        if (!isLicenceValid()) {
            return "Expired";
        }
        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), licenceExpiry);
        return daysLeft <= 30 ? "Expiring soon" : "Valid";
    }

    /** A driver can take a trip only if they are active, available and have a valid licence. */
    public boolean canAcceptTrips() {
        return isActive() && isLicenceValid() && availability == DriverAvailability.AVAILABLE;
    }

    // ----- Polymorphism: Driver's own versions of the abstract methods -----

    @Override
    public String getRoleLabel() {
        return "Driver";
    }

    @Override
    public String getProfileSummary() {
        return "Driver with " + yearsOfExperience + " year(s) experience, licence " + getLicenceStatus().toLowerCase();
    }

    // ----- Getters and setters -----

    public String getLicenceNumber() {
        return licenceNumber;
    }

    public void setLicenceNumber(String licenceNumber) {
        this.licenceNumber = licenceNumber;
    }

    public LocalDate getLicenceExpiry() {
        return licenceExpiry;
    }

    public void setLicenceExpiry(LocalDate licenceExpiry) {
        this.licenceExpiry = licenceExpiry;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public DriverAvailability getAvailability() {
        return availability;
    }

    public void setAvailability(DriverAvailability availability) {
        this.availability = availability;
    }
}
