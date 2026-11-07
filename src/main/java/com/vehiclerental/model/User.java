package com.vehiclerental.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Parent class for Customer, Driver and Admin.
 * Common details (name, email, phone, password, role) are saved in the "users" table.
 * Each child class saves its extra details in its own table (customers, drivers, admins).
 *
 * OOP:
 *  - Abstraction: this class is abstract, so you can never create a plain "User".
 *  - Inheritance: Customer, Driver and Admin all "extend User".
 *  - Encapsulation: all fields are private, with getters and setters.
 *  - Polymorphism: each child class gives its own version of getRoleLabel() and getProfileSummary().
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must be 100 characters or less")
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Size(max = 100, message = "Email must be 100 characters or less")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must have exactly 10 digits")
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    // Information hiding: the password is stored as a BCrypt hash and never shown on any page
    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    // ----- Abstract methods: every child class MUST write its own version -----

    /** A friendly name for the role, for example "Customer". */
    public abstract String getRoleLabel();

    /** A one-line summary with the details that only this type of user has. */
    public abstract String getProfileSummary();

    // ----- Getters and setters -----

    public Long getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    // protected: only the child classes can set the role (in their constructors)
    protected void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // Polymorphism: overrides Object.toString(). The password is left out on purpose.
    @Override
    public String toString() {
        return getRoleLabel() + "{id=" + userId + ", name='" + fullName + "', email='" + email + "'}";
    }
}
