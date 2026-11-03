-- Member 3: optional setup for an empty local MySQL installation.
-- Creates only the vehicle module's database/table. No DROP, ALTER, or data reset.
-- Existing tables are left unchanged; this is not a migration of database/schema.sql.
-- The bookings module and its vehicle_id foreign key are not created here.
CREATE DATABASE IF NOT EXISTS vehicle_rental_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE vehicle_rental_db;

CREATE TABLE IF NOT EXISTS vehicles (
    vehicle_id BIGINT NOT NULL AUTO_INCREMENT,
    vehicle_type VARCHAR(10) NOT NULL,
    registration_number VARCHAR(20) NOT NULL,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    manufacture_year INT NOT NULL,
    seating_capacity INT NOT NULL,
    colour VARCHAR(30) NULL,
    status ENUM('AVAILABLE', 'BOOKED', 'MAINTENANCE') NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (vehicle_id),
    UNIQUE KEY uk_vehicles_registration (registration_number),
    CONSTRAINT chk_vehicles_type CHECK (vehicle_type IN ('CAR', 'VAN', 'SUV'))
) ENGINE=InnoDB;
