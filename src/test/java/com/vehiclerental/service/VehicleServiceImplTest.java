package com.vehiclerental.service;

import com.vehiclerental.model.Car;
import com.vehiclerental.model.Van;
import com.vehiclerental.model.Vehicle;
import com.vehiclerental.model.VehicleForm;
import com.vehiclerental.model.VehicleStatus;
import com.vehiclerental.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VehicleServiceImplTest {

    private VehicleRepository repository;
    private VehicleService service;

    @BeforeEach
    void setUp() {
        repository = mock(VehicleRepository.class);
        service = new VehicleServiceImpl(repository);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void blankSearchListsVehiclesInRegistrationOrder(String keyword) {
        when(repository.findAll(Sort.by("registrationNumber"))).thenReturn(List.of(new Car()));
        assertThat(service.search(keyword)).hasSize(1);
        verify(repository).findAll(Sort.by("registrationNumber"));
    }

    @Test
    void searchTrimsKeywordForAllThreeFields() {
        when(repository.findByRegistrationNumberContainingIgnoreCaseOrBrandContainingIgnoreCaseOrModelContainingIgnoreCaseOrderByRegistrationNumberAsc(
                "toyota", "toyota", "toyota")).thenReturn(List.of(new Car(), new Van()));
        assertThat(service.search("  toyota  ")).hasSize(2);
    }

    @Test
    void duplicateChecksTrimRegistrationAndExcludeTheEditedId() {
        when(repository.existsByRegistrationNumberIgnoreCase("cab-1234")).thenReturn(true);
        when(repository.existsByRegistrationNumberIgnoreCaseAndVehicleIdNot("cab-1234", 7L)).thenReturn(false);
        assertThat(service.isRegistrationTaken(" cab-1234 ", null)).isTrue();
        assertThat(service.isRegistrationTaken(" cab-1234 ", 7L)).isFalse();
        assertThat(service.isRegistrationTaken(null, null)).isFalse();
    }

    @Test
    void createUsesFactoryAndNormalizesFormData() {
        when(repository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Vehicle vehicle = service.create(form());
        assertThat(vehicle).isInstanceOf(Car.class);
        assertThat(vehicle.getRegistrationNumber()).isEqualTo("CAD-4321");
        assertThat(vehicle.getBrand()).isEqualTo("Honda");
        assertThat(vehicle.getModel()).isEqualTo("Fit");
        assertThat(vehicle.getColour()).isEqualTo("White");
        assertThat(vehicle.getManufactureYear()).isEqualTo(2019);
        assertThat(vehicle.getSeatingCapacity()).isEqualTo(4);
        assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
    }

    @Test
    void updateRetainsExistingSubtypeEvenWhenFormAttemptsToChangeIt() {
        Vehicle car = new Car();
        car.setVehicleId(7L);
        when(repository.findById(7L)).thenReturn(Optional.of(car));
        when(repository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));
        VehicleForm form = form();
        form.setVehicleType("VAN");
        form.setStatus(VehicleStatus.MAINTENANCE);
        assertThat(service.update(7L, form)).isSameAs(car);
        ArgumentCaptor<Vehicle> saved = ArgumentCaptor.forClass(Vehicle.class);
        verify(repository).save(saved.capture());
        assertThat(saved.getValue()).isInstanceOf(Car.class);
        assertThat(saved.getValue().getStatus()).isEqualTo(VehicleStatus.MAINTENANCE);
    }

    @Test
    void updatingMissingVehicleDoesNotSave() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(99L, form()))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Vehicle not found.");
        verify(repository, never()).save(any());
        assertThat(service.findById(99L)).isEmpty();
    }

    @Test
    void deleteFlushesAndPropagatesBookingConstraintFailures() {
        when(repository.findByIdForUpdate(7L)).thenReturn(Optional.of(new Car()));
        when(repository.countBookingTables()).thenReturn(1L);
        when(repository.countBookingVehicleIdColumns()).thenReturn(1L);
        when(repository.countBookingReferences(7L)).thenReturn(0L);
        doThrow(new DataIntegrityViolationException("booking foreign key")).when(repository).flush();
        assertThatThrownBy(() -> service.delete(7L)).isInstanceOf(DataIntegrityViolationException.class);
        var order = inOrder(repository);
        order.verify(repository).deleteById(7L);
        order.verify(repository).flush();
    }

    @Test
    void missingBookingsTableAllowsDeletion() {
        when(repository.findByIdForUpdate(8L)).thenReturn(Optional.of(new Car()));
        when(repository.countBookingTables()).thenReturn(0L);
        service.delete(8L);
        verify(repository).deleteById(8L);
        verify(repository).flush();
        verify(repository, never()).countBookingVehicleIdColumns();
        verify(repository, never()).countBookingReferences(any());
    }

    @Test
    void unreferencedVehicleIsDeletedWithoutRequiringForeignKey() {
        when(repository.findByIdForUpdate(8L)).thenReturn(Optional.of(new Car()));
        when(repository.countBookingTables()).thenReturn(1L);
        when(repository.countBookingVehicleIdColumns()).thenReturn(1L);
        service.delete(8L);
        var order = inOrder(repository);
        order.verify(repository).findByIdForUpdate(8L);
        order.verify(repository).countBookingTables();
        order.verify(repository).countBookingVehicleIdColumns();
        order.verify(repository).countBookingReferences(8L);
        order.verify(repository).deleteById(8L);
        order.verify(repository).flush();
    }

    @Test
    void referencedVehicleIsProtectedEvenWithASetNullForeignKey() {
        when(repository.findByIdForUpdate(7L)).thenReturn(Optional.of(new Car()));
        when(repository.countBookingTables()).thenReturn(1L);
        when(repository.countBookingVehicleIdColumns()).thenReturn(1L);
        when(repository.countBookingReferences(7L)).thenReturn(1L);
        assertThatThrownBy(() -> service.delete(7L)).isInstanceOf(DataIntegrityViolationException.class);
        verify(repository, never()).deleteById(any());
        verify(repository, never()).flush();
    }

    @Test
    void existingBookingsTableWithoutVehicleColumnBlocksDeletion() {
        when(repository.findByIdForUpdate(7L)).thenReturn(Optional.of(new Car()));
        when(repository.countBookingTables()).thenReturn(1L);
        when(repository.countBookingVehicleIdColumns()).thenReturn(0L);
        assertThatThrownBy(() -> service.delete(7L)).isInstanceOf(IllegalStateException.class)
                .hasMessage("The bookings.vehicle_id column could not be verified.");
        verify(repository, never()).countBookingReferences(any());
        verify(repository, never()).deleteById(any());
    }

    @Test
    void cannotVerifyBookingsTableBlocksDeletion() {
        when(repository.findByIdForUpdate(7L)).thenReturn(Optional.of(new Car()));
        when(repository.countBookingTables()).thenThrow(new DataAccessResourceFailureException("Unable to inspect booking schema"));
        assertThatThrownBy(() -> service.delete(7L)).isInstanceOf(DataAccessResourceFailureException.class);
        verify(repository, never()).deleteById(any());
    }

    @Test
    void vehicleRemovedBeforeLockingIsReportedAsMissing() {
        when(repository.findByIdForUpdate(7L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.delete(7L)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Vehicle not found.");
        verify(repository, never()).deleteById(any());
    }

    private VehicleForm form() {
        VehicleForm form = new VehicleForm();
        form.setVehicleType("car");
        form.setRegistrationNumber(" cad-4321 ");
        form.setBrand(" Honda ");
        form.setModel(" Fit ");
        form.setManufactureYear(2019);
        form.setSeatingCapacity(4);
        form.setColour(" White ");
        return form;
    }
}
