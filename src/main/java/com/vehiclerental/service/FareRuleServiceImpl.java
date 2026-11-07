package com.vehiclerental.service;

import com.vehiclerental.model.FareCalculator;
import com.vehiclerental.model.FareRule;
import com.vehiclerental.model.NightFareCalculator;
import com.vehiclerental.repository.FareRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Business rules and database calls for fare rules.
 */
@Service
public class FareRuleServiceImpl implements FareRuleService {

    private final FareRuleRepository fareRuleRepository;

    public FareRuleServiceImpl(FareRuleRepository fareRuleRepository) {
        this.fareRuleRepository = fareRuleRepository;
    }

    @Override
    public List<FareRule> findAll() {
        return fareRuleRepository.findAllByOrderByVehicleTypeAsc();
    }

    @Override
    public Optional<FareRule> findById(Long id) {
        return fareRuleRepository.findById(id);
    }

    @Override
    public boolean isVehicleTypeTaken(String vehicleType, Long excludeFareRuleId) {
        if (excludeFareRuleId == null) {
            return fareRuleRepository.existsByVehicleType(vehicleType);
        }
        return fareRuleRepository.existsByVehicleTypeAndFareRuleIdNot(vehicleType, excludeFareRuleId);
    }

    @Override
    @Transactional
    public FareRule create(FareRule fareRule) {
        return fareRuleRepository.save(fareRule);
    }

    @Override
    @Transactional
    public FareRule update(Long id, FareRule formData) {
        FareRule existing = fareRuleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fare rule not found: " + id));
        existing.setVehicleType(formData.getVehicleType());
        existing.setBaseFare(formData.getBaseFare());
        existing.setPricePerKm(formData.getPricePerKm());
        existing.setMinimumFare(formData.getMinimumFare());
        existing.setActive(formData.isActive());
        return fareRuleRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        fareRuleRepository.deleteById(id);
    }

    @Override
    public Optional<FareCalculator> getCalculator(String vehicleType, boolean nightTrip) {
        Optional<FareRule> rule = fareRuleRepository.findByVehicleTypeAndActiveTrue(vehicleType);
        if (rule.isEmpty()) {
            return Optional.empty();
        }
        // POLYMORPHISM: both choices are "FareCalculator", but calculateFare() works differently
        FareCalculator calculator = nightTrip ? new NightFareCalculator(rule.get()) : rule.get();
        return Optional.of(calculator);
    }
}
