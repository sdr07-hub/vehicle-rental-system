package com.vehiclerental.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;

import java.time.LocalDateTime;

/**
 * Parent class for every database entity in the project.
 * It holds the "created_at" date that all tables share.
 *
 * OOP: Abstraction (abstract class) and Inheritance (all entities extend it).
 */
@MappedSuperclass
public abstract class BaseEntity {

    // Encapsulation: the field is private and can only be read through a getter
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Runs automatically just before a new row is inserted
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}