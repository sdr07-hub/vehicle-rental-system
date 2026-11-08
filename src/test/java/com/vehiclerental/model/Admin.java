package com.vehiclerental.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * A system administrator.
 * Common details come from User (users table); the admin level is saved in the admins table.
 *
 * OOP: Inheritance (extends User), Polymorphism (own getRoleLabel / getProfileSummary).
 */
@Entity
@Table(name = "admins")
@PrimaryKeyJoinColumn(name = "admin_id")
public class Admin extends User {

    @NotNull(message = "Please choose an admin level")
    @Enumerated(EnumType.STRING)
    @Column(name = "admin_level", nullable = false, length = 20)
    private AdminLevel adminLevel = AdminLevel.STAFF;

    public Admin() {
        setRole(UserRole.ADMIN);
    }

    /** Only super admins may add or remove other admins. */
    public boolean canManageAdmins() {
        return adminLevel == AdminLevel.SUPER_ADMIN;
    }

    @Override
    public String getRoleLabel() {
        return "Admin";
    }

    @Override
    public String getProfileSummary() {
        return adminLevel.getLabel() + (canManageAdmins() ? " - can manage other admins" : "");
    }

    public AdminLevel getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(AdminLevel adminLevel) {
        this.adminLevel = adminLevel;
    }
}
