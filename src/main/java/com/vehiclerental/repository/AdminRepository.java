package com.vehiclerental.repository;

import com.vehiclerental.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Database access for admins, plus simple counts for the admin dashboard.
 * The count queries read other tables directly, so this module does not need other members' classes.
 */
public interface AdminRepository extends JpaRepository<Admin, Long> {

    List<Admin> findAllByOrderByFullNameAsc();

    @Query(value = "SELECT COUNT(*) FROM customers", nativeQuery = true)
    long countCustomers();

    @Query(value = "SELECT COUNT(*) FROM drivers", nativeQuery = true)
    long countDrivers();

    @Query(value = "SELECT COUNT(*) FROM vehicles", nativeQuery = true)
    long countVehicles();

    @Query(value = "SELECT COUNT(*) FROM vehicles WHERE status = 'AVAILABLE'", nativeQuery = true)
    long countAvailableVehicles();

    @Query(value = "SELECT COUNT(*) FROM bookings", nativeQuery = true)
    long countBookings();

    @Query(value = "SELECT COUNT(*) FROM bookings WHERE status = 'PENDING'", nativeQuery = true)
    long countPendingBookings();

    @Query(value = "SELECT COUNT(*) FROM feedback WHERE status <> 'RESOLVED' AND status <> 'CLOSED'", nativeQuery = true)
    long countOpenFeedback();
}
