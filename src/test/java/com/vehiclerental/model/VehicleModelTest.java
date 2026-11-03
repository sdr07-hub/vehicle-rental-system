package com.vehiclerental.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VehicleModelTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @ParameterizedTest
    @CsvSource({"CAR, Car, 5", "van, Van, 15", "sUv, SUV, 7"})
    void factorySelectsSubtypeAndPolymorphicSeatLimits(String code, String name, int maxSeats) {
        Vehicle vehicle = VehicleFactory.create(code);
        assertThat(VehicleFactory.isValidType(code)).isTrue();
        assertThat(vehicle.getTypeName()).isEqualTo(name);
        assertThat(vehicle.getMaxSeats()).isEqualTo(maxSeats);
        vehicle.setSeatingCapacity(0);
        assertThat(vehicle.isSeatingCapacityValid()).isFalse();
        vehicle.setSeatingCapacity(1);
        assertThat(vehicle.isSeatingCapacityValid()).isTrue();
        vehicle.setSeatingCapacity(maxSeats);
        assertThat(vehicle.isSeatingCapacityValid()).isTrue();
        vehicle.setSeatingCapacity(maxSeats + 1);
        assertThat(vehicle.isSeatingCapacityValid()).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"MOTORCYCLE", " ", " CAR "})
    void factoryRejectsUnsupportedTypes(String code) {
        assertThat(VehicleFactory.isValidType(code)).isFalse();
        assertThatThrownBy(() -> VehicleFactory.create(code)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void subtypeDescriptionsUseTheActualSeats() {
        Vehicle car = new Car();
        car.setSeatingCapacity(4);
        assertThat(car.getDescription()).isEqualTo("Comfortable car for up to 4 passengers - best for city trips.");
        Vehicle van = new Van();
        van.setSeatingCapacity(12);
        assertThat(van.getDescription()).isEqualTo("Spacious van for groups of up to 12 - good for families and tours.");
        Vehicle suv = new Suv();
        suv.setSeatingCapacity(7);
        assertThat(suv.getDescription()).isEqualTo("Powerful SUV for up to 7 passengers - ideal for hill country and long trips.");
    }

    @Test
    void editFormCopiesEveryEditableFieldAndTheSavedType() {
        Vehicle car = new Car();
        car.setVehicleId(1L);
        car.setRegistrationNumber("CAB-1234");
        car.setBrand("Toyota");
        car.setModel("Axio");
        car.setManufactureYear(2019);
        car.setSeatingCapacity(5);
        car.setColour("White");
        assertThat(car.isAvailable()).isTrue();
        car.setStatus(VehicleStatus.MAINTENANCE);
        assertThat(car.isAvailable()).isFalse();
        VehicleForm form = VehicleForm.fromVehicle(car);
        assertThat(form.getVehicleType()).isEqualTo("CAR");
        assertThat(form.getRegistrationNumber()).isEqualTo("CAB-1234");
        assertThat(form.getBrand()).isEqualTo("Toyota");
        assertThat(form.getModel()).isEqualTo("Axio");
        assertThat(form.getManufactureYear()).isEqualTo(2019);
        assertThat(form.getSeatingCapacity()).isEqualTo(5);
        assertThat(form.getColour()).isEqualTo("White");
        assertThat(form.getStatus()).isEqualTo(VehicleStatus.MAINTENANCE);
        assertThat(car.getDisplayName()).isEqualTo("Toyota Axio (CAB-1234)");
    }

    @Test
    void blankFormReportsRequiredFields() {
        assertThat(validator.validate(new VehicleForm()))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("vehicleType", "registrationNumber", "brand", "model", "manufactureYear", "seatingCapacity");
    }

    @Test
    void formatAndSizeAndRangeValidationRejectInvalidInput() {
        VehicleForm form = validForm();
        assertThat(validator.validate(form)).isEmpty();
        form.setRegistrationNumber("12345");
        form.setBrand("B".repeat(51));
        form.setModel("M".repeat(51));
        form.setColour("C".repeat(31));
        form.setManufactureYear(1989);
        form.setSeatingCapacity(51);
        form.setStatus(null);
        assertThat(validator.validate(form)).extracting(violation -> violation.getPropertyPath().toString())
                .contains("registrationNumber", "brand", "model", "colour", "manufactureYear", "seatingCapacity", "status");
        assertThat(validator.validate(form)).extracting(violation -> violation.getMessage())
                .contains("Use the format ABC-1234 or AB-1234");
    }

    @ParameterizedTest
    @ValueSource(strings = {"AB-1234", "cab-1234", " CAB-1234 "})
    void acceptsDocumentedRegistrationFormatsAndSurroundingWhitespace(String registration) {
        VehicleForm form = validForm();
        form.setRegistrationNumber(registration);
        assertThat(validator.validate(form)).isEmpty();
    }

    private VehicleForm validForm() {
        VehicleForm form = new VehicleForm();
        form.setVehicleType("CAR");
        form.setRegistrationNumber("CAB-1234");
        form.setBrand("Toyota");
        form.setModel("Axio");
        form.setManufactureYear(2019);
        form.setSeatingCapacity(5);
        return form;
    }
}
