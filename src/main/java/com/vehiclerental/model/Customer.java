package com.vehiclerental.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.Period;

/**
 * A customer who books vehicles.
 * Name, email, phone and password come from the parent class User (users table).
 * Address and date of birth are saved in the customers table.
 *
 * OOP: Inheritance (extends User), Encapsulation (private fields),
 *      Polymorphism (overrides getRoleLabel and getProfileSummary).
 */
@Entity
@Table(name = "customers")
@PrimaryKeyJoinColumn(name = "customer_id")
public class Customer extends User {

    @Size(max = 255, message = "Address must be 255 characters or less")
    @Column(name = "address", length = 255)
    private String address;

    @Past(message = "Date of birth must be in the past")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    public Customer() {
        // every new Customer object automatically gets the CUSTOMER role
        setRole(UserRole.CUSTOMER);
    }

    /** Works out the customer's age from the date of birth (returns null if not given). */
    public Integer getAge() {
        if (dateOfBirth == null) {
            return null;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    @Override
    public String getRoleLabel() {
        return "Customer";
    }

    @Override
    public String getProfileSummary() {
        String place = (address == null || address.isBlank()) ? "No address" : address;
        return "Customer living at " + place;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
