-- =====================================================================
-- Vehicle Rental System - database structure (MySQL 8)
-- Run this ONCE in MySQL Workbench (connected as root).
-- WARNING: it deletes the existing tables and creates them again (empty).
-- =====================================================================

CREATE DATABASE IF NOT EXISTS vehicle_rental_db;
USE vehicle_rental_db;

-- Remove old tables (child tables first)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS feedback;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS fare_rules;
DROP TABLE IF EXISTS vehicles;
DROP TABLE IF EXISTS admins;
DROP TABLE IF EXISTS drivers;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
-- users: common details for every person (parent table)
-- ---------------------------------------------------------------------
CREATE TABLE users (
                       user_id     BIGINT       NOT NULL AUTO_INCREMENT,
                       full_name   VARCHAR(100) NOT NULL,
                       email       VARCHAR(100) NOT NULL,
                       phone       VARCHAR(20)  NOT NULL,
                       password    VARCHAR(255) NOT NULL,
                       role        VARCHAR(20)  NOT NULL,
                       active      BOOLEAN      NOT NULL DEFAULT TRUE,
                       created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       PRIMARY KEY (user_id),
                       UNIQUE KEY uk_users_email (email)
);

-- ---------------------------------------------------------------------
-- customers: extra details for users with role CUSTOMER
-- customer_id is the same number as users.user_id
-- ---------------------------------------------------------------------
CREATE TABLE customers (
                           customer_id    BIGINT       NOT NULL,
                           address        VARCHAR(255) NULL,
                           date_of_birth  DATE         NULL,
                           PRIMARY KEY (customer_id),
                           CONSTRAINT fk_customers_user FOREIGN KEY (customer_id)
                               REFERENCES users (user_id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- drivers: extra details for users with role DRIVER
-- ---------------------------------------------------------------------
CREATE TABLE drivers (
                         driver_id            BIGINT      NOT NULL,
                         licence_number       VARCHAR(30) NOT NULL,
                         licence_expiry       DATE        NOT NULL,
                         years_of_experience  INT         NOT NULL DEFAULT 0,
                         availability         VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
                         PRIMARY KEY (driver_id),
                         UNIQUE KEY uk_drivers_licence (licence_number),
                         CONSTRAINT fk_drivers_user FOREIGN KEY (driver_id)
                             REFERENCES users (user_id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- admins: extra details for users with role ADMIN
-- ---------------------------------------------------------------------
CREATE TABLE admins (
                        admin_id     BIGINT      NOT NULL,
                        admin_level  VARCHAR(20) NOT NULL DEFAULT 'STAFF',
                        PRIMARY KEY (admin_id),
                        CONSTRAINT fk_admins_user FOREIGN KEY (admin_id)
                            REFERENCES users (user_id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- vehicles: one table for all vehicle types (CAR, VAN, SUV)
-- ---------------------------------------------------------------------
CREATE TABLE vehicles (
                          vehicle_id           BIGINT      NOT NULL AUTO_INCREMENT,
                          vehicle_type         VARCHAR(10) NOT NULL,
                          registration_number  VARCHAR(20) NOT NULL,
                          brand                VARCHAR(50) NOT NULL,
                          model                VARCHAR(50) NOT NULL,
                          manufacture_year     INT         NOT NULL,
                          seating_capacity     INT         NOT NULL,
                          colour               VARCHAR(30) NULL,
                          status               VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
                          created_at           DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          PRIMARY KEY (vehicle_id),
                          UNIQUE KEY uk_vehicles_registration (registration_number)
);

-- ---------------------------------------------------------------------
-- fare_rules: price settings for each vehicle type
-- ---------------------------------------------------------------------
CREATE TABLE fare_rules (
                            fare_rule_id   BIGINT        NOT NULL AUTO_INCREMENT,
                            vehicle_type   VARCHAR(10)   NOT NULL,
                            base_fare      DECIMAL(10,2) NOT NULL,
                            price_per_km   DECIMAL(10,2) NOT NULL,
                            minimum_fare   DECIMAL(10,2) NOT NULL,
                            active         BOOLEAN       NOT NULL DEFAULT TRUE,
                            created_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            PRIMARY KEY (fare_rule_id),
                            UNIQUE KEY uk_fare_rules_type (vehicle_type)
);

-- ---------------------------------------------------------------------
-- bookings: a customer's trip request
-- ---------------------------------------------------------------------
CREATE TABLE bookings (
                          booking_id       BIGINT        NOT NULL AUTO_INCREMENT,
                          customer_id      BIGINT        NOT NULL,
                          vehicle_id       BIGINT        NOT NULL,
                          driver_id        BIGINT        NULL,
                          pickup_location  VARCHAR(150)  NOT NULL,
                          drop_location    VARCHAR(150)  NOT NULL,
                          pickup_time      DATETIME      NOT NULL,
                          distance_km      DECIMAL(8,2)  NOT NULL,
                          estimated_fare   DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                          status           VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
                          created_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          PRIMARY KEY (booking_id),
                          CONSTRAINT fk_bookings_customer FOREIGN KEY (customer_id)
                              REFERENCES customers (customer_id),
                          CONSTRAINT fk_bookings_vehicle FOREIGN KEY (vehicle_id)
                              REFERENCES vehicles (vehicle_id),
                          CONSTRAINT fk_bookings_driver FOREIGN KEY (driver_id)
                              REFERENCES drivers (driver_id) ON DELETE SET NULL
);

-- ---------------------------------------------------------------------
-- feedback: ratings, general feedback and complaints
-- ---------------------------------------------------------------------
CREATE TABLE feedback (
                          feedback_id     BIGINT        NOT NULL AUTO_INCREMENT,
                          customer_id     BIGINT        NOT NULL,
                          booking_id      BIGINT        NULL,
                          feedback_type   VARCHAR(20)   NOT NULL,
                          rating          INT           NOT NULL,
                          subject         VARCHAR(150)  NOT NULL,
                          message         VARCHAR(1000) NOT NULL,
                          status          VARCHAR(20)   NOT NULL DEFAULT 'OPEN',
                          admin_response  VARCHAR(1000) NULL,
                          created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          PRIMARY KEY (feedback_id),
                          CONSTRAINT fk_feedback_customer FOREIGN KEY (customer_id)
                              REFERENCES customers (customer_id) ON DELETE CASCADE,
                          CONSTRAINT fk_feedback_booking FOREIGN KEY (booking_id)
                              REFERENCES bookings (booking_id) ON DELETE SET NULL
);
