package com.vehiclerental.service;

import com.vehiclerental.model.FareCalculator;
import com.vehiclerental.model.FareRule;

import java.util.List;
import java.util.Optional;

/**
 * What the Fare part of the module can do.
 * OOP: Abstraction - FareRuleServiceImpl contains the details.
 */
public interface FareRuleService {

    List<FareRule> findAll();

    Optional<FareRule> findById(Long id);

    boolean isVehicleTypeTaken(String vehicleType, Long excludeFareRuleId);

    FareRule create(FareRule fareRule);

    FareRule update(Long id, FareRule formData);

    void delete(Long id);

    /** Picks the right calculator: the normal fare rule, or the night version of it. Empty if no rule. */
    Optional<FareCalculator> getCalculator(String vehicleType, boolean nightTrip);
}
