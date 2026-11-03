package com.vehiclerental.repository;

import com.vehiclerental.model.Car;
import com.vehiclerental.model.Suv;
import com.vehiclerental.model.Van;
import com.vehiclerental.model.Vehicle;
import com.vehiclerental.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Opt in only with the shared schema and documented samples on a MySQL test database. */
@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@Transactional
@EnabledIfEnvironmentVariable(named = "VEHICLE_MYSQL_TESTS", matches = "true")
class VehicleRepositoryMySqlTest {

    @Autowired
    private VehicleRepository repository;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private VehicleService service;

    @Test
    void allSubtypesShareOneTableWithTheirDiscriminators() {
        Vehicle car = save(new Car(), "ZT-9001", "Axio", 5);
        Vehicle van = save(new Van(), "ZT-9002", "KDH", 12);
        Vehicle suv = save(new Suv(), "ZT-9003", "RAV4", 7);
        for (Vehicle vehicle : new Vehicle[]{car, van, suv}) {
            assertThat(jdbc.queryForObject("SELECT vehicle_type FROM vehicles WHERE vehicle_id = ?",
                    String.class, vehicle.getVehicleId())).isEqualTo(vehicle.getTypeCode());
        }
        assertThat(repository.findAllById(java.util.List.of(car.getVehicleId(), van.getVehicleId(), suv.getVehicleId())))
                .extracting(Vehicle::getTypeCode).containsExactlyInAnyOrder("CAR", "VAN", "SUV");
    }

    @Test
    void searchMatchesEverySearchableFieldIgnoringCaseAndSortsResults() {
        save(new Van(), "ZT-9002", "KDH", 12);
        save(new Car(), "ZT-9001", "Axio", 5);
        assertThat(search("module-test-toyota")).extracting(Vehicle::getRegistrationNumber)
                .containsExactly("ZT-9001", "ZT-9002");
        assertThat(search("zt-9001")).extracting(Vehicle::getModel).containsExactly("Axio");
        assertThat(search("kDh")).extracting(Vehicle::getRegistrationNumber).contains("ZT-9002");
        assertThat(search("no-matching-vehicle-7ce81d")).isEmpty();
    }

    @Test
    void registrationChecksIgnoreCaseAndExcludeTheEditedVehicle() {
        Vehicle car = save(new Car(), "ZT-9001", "Axio", 5);
        assertThat(repository.existsByRegistrationNumberIgnoreCase("zt-9001")).isTrue();
        assertThat(repository.existsByRegistrationNumberIgnoreCaseAndVehicleIdNot("zt-9001", car.getVehicleId()))
                .isFalse();
        assertThat(repository.existsByRegistrationNumberIgnoreCaseAndVehicleIdNot("zt-9001", -1L)).isTrue();
    }

    @Test
    void duplicateRegistrationIsAlsoProtectedByTheDatabase() {
        save(new Car(), "ZT-9001", "Axio", 5);
        assertThatThrownBy(() -> save(new Van(), "zt-9001", "KDH", 12))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void documentedSamplesIncludeAllTypesAndToyotaModels() {
        assertThat(repository.findAll()).extracting(Vehicle::getTypeCode).contains("CAR", "VAN", "SUV");
        assertThat(search("toyota")).extracting(Vehicle::getModel).contains("Axio", "KDH");
    }

    @Test
    void bookingOnePreventsDeletingTheDocumentedSampleCar() {
        Long id = jdbc.queryForObject("SELECT vehicle_id FROM vehicles WHERE registration_number = 'CAB-1234'", Long.class);
        assertThat(jdbc.queryForObject("SELECT vehicle_id FROM bookings WHERE booking_id = 1", Long.class)).isEqualTo(id);
        assertThat(repository.countBookingVehicleForeignKeys()).isPositive();
        assertThatThrownBy(() -> service.delete(id)).isInstanceOf(DataIntegrityViolationException.class);
        assertThat(jdbc.queryForObject("SELECT vehicle_id FROM bookings WHERE booking_id = 1", Long.class)).isEqualTo(id);
    }

    @Test
    void unreferencedVehicleCanBeDeleted() {
        Vehicle car = save(new Car(), "ZT-9001", "Axio", 5);
        service.delete(car.getVehicleId());
        assertThat(repository.findById(car.getVehicleId())).isEmpty();
    }

    private java.util.List<Vehicle> search(String keyword) {
        return repository.findByRegistrationNumberContainingIgnoreCaseOrBrandContainingIgnoreCaseOrModelContainingIgnoreCaseOrderByRegistrationNumberAsc(
                keyword, keyword, keyword);
    }

    private Vehicle save(Vehicle vehicle, String registration, String model, int seats) {
        vehicle.setRegistrationNumber(registration);
        vehicle.setBrand("Module-Test-Toyota");
        vehicle.setModel(model);
        vehicle.setManufactureYear(2019);
        vehicle.setSeatingCapacity(seats);
        return repository.saveAndFlush(vehicle);
    }
}
