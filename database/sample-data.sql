-- =====================================================================
-- Vehicle Rental System - sample data for testing
-- Run AFTER schema.sql, in MySQL Workbench (connected as root).
-- Every sample user's password is: password123  (stored as a BCrypt hash)
-- =====================================================================

USE vehicle_rental_db;

-- Users (parent rows) ---------------------------------------------------
INSERT INTO users (user_id, full_name, email, phone, password, role, active) VALUES
                                                                                 (1, 'Nimal Perera',     'nimal@example.com',   '0771234567', '$2a$10$TBAB/iSFWgASZvrTgKnqHeWCbmwt6WU2tE6u6d7G0lS6rsa62Mfgi', 'CUSTOMER', TRUE),
                                                                                 (2, 'Kavindi Silva',    'kavindi@example.com', '0712345678', '$2a$10$TBAB/iSFWgASZvrTgKnqHeWCbmwt6WU2tE6u6d7G0lS6rsa62Mfgi', 'CUSTOMER', TRUE),
                                                                                 (3, 'Ruwan Jayasinghe', 'ruwan@example.com',   '0763456789', '$2a$10$TBAB/iSFWgASZvrTgKnqHeWCbmwt6WU2tE6u6d7G0lS6rsa62Mfgi', 'DRIVER',   TRUE),
                                                                                 (4, 'Saman Kumara',     'saman@example.com',   '0754567890', '$2a$10$TBAB/iSFWgASZvrTgKnqHeWCbmwt6WU2tE6u6d7G0lS6rsa62Mfgi', 'DRIVER',   TRUE),
                                                                                 (5, 'Admin User',       'admin@example.com',   '0705678901', '$2a$10$TBAB/iSFWgASZvrTgKnqHeWCbmwt6WU2tE6u6d7G0lS6rsa62Mfgi', 'ADMIN',    TRUE);

-- Child rows (same id as the user) ----------------------------------------
INSERT INTO customers (customer_id, address, date_of_birth) VALUES
                                                                (1, '12 Galle Road, Colombo 03', '1998-04-12'),
                                                                (2, '45 Kandy Road, Kadawatha',  '2001-09-30');

INSERT INTO drivers (driver_id, licence_number, licence_expiry, years_of_experience, availability) VALUES
                                                                                                       (3, 'B1234567', '2028-06-30', 6, 'AVAILABLE'),
                                                                                                       (4, 'B7654321', '2027-01-15', 3, 'AVAILABLE');

INSERT INTO admins (admin_id, admin_level) VALUES
    (5, 'SUPER_ADMIN');

-- Vehicles ----------------------------------------------------------------
INSERT INTO vehicles (vehicle_id, vehicle_type, registration_number, brand, model, manufacture_year, seating_capacity, colour, status) VALUES
                                                                                                                                           (1, 'CAR', 'CAB-1234', 'Toyota',     'Axio',     2018, 4,  'White',  'AVAILABLE'),
                                                                                                                                           (2, 'VAN', 'PE-5678',  'Toyota',     'KDH',      2017, 12, 'Silver', 'AVAILABLE'),
                                                                                                                                           (3, 'SUV', 'CBA-9012', 'Mitsubishi', 'Montero',  2020, 7,  'Black',  'AVAILABLE');

-- Fare rules (one per vehicle type) -----------------------------------------
INSERT INTO fare_rules (vehicle_type, base_fare, price_per_km, minimum_fare, active) VALUES
                                                                                         ('CAR', 150.00,  95.00,  300.00, TRUE),
                                                                                         ('VAN', 250.00, 130.00,  500.00, TRUE),
                                                                                         ('SUV', 300.00, 150.00,  600.00, TRUE);

-- Bookings ----------------------------------------------------------------
INSERT INTO bookings (booking_id, customer_id, vehicle_id, driver_id, pickup_location, drop_location, pickup_time, distance_km, estimated_fare, status) VALUES
                                                                                                                                                            (1, 1, 1, 3,    'Colombo Fort',  'Bambalapitiya', '2026-11-10 09:30:00', 6.50,  767.50, 'CONFIRMED'),
                                                                                                                                                            (2, 2, 2, NULL, 'Kadawatha',     'Negombo',       '2026-11-12 14:00:00', 25.00, 3500.00, 'PENDING');

-- Feedback ----------------------------------------------------------------
INSERT INTO feedback (customer_id, booking_id, feedback_type, rating, subject, message, status) VALUES
                                                                                                    (1, 1, 'GENERAL',   5, 'Great ride',    'The driver was on time and very friendly.', 'OPEN'),
                                                                                                    (2, 2, 'COMPLAINT', 2, 'Late pickup',   'The vehicle arrived 20 minutes late.',      'OPEN');
