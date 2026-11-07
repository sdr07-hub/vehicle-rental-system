package com.vehiclerental.repository;

import com.vehiclerental.model.FareRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Database access for fare rules.
 */
public interface FareRuleRepository extends JpaRepository<FareRule, Long> {

    List<FareRule> findAllByOrderByVehicleTypeAsc();

    Optional<FareRule> findByVehicleTypeAndActiveTrue(String vehicleType);

    boolean existsByVehicleType(String vehicleType);

    boolean existsByVehicleTypeAndFareRuleIdNot(String vehicleType, Long fareRuleId);
}
