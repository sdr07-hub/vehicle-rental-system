package com.vehiclerental.controller;

import com.vehiclerental.model.Car;
import com.vehiclerental.model.Vehicle;
import com.vehiclerental.model.VehicleFactory;
import com.vehiclerental.model.VehicleForm;
import com.vehiclerental.model.VehicleStatus;
import com.vehiclerental.service.VehicleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/** MVC/binding tests use a mocked service; JSP rendering and real MySQL are separate checks. */
class VehicleControllerTest {

    private VehicleService service;
    private LocalValidatorFactoryBean validator;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(VehicleService.class);
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mvc = MockMvcBuilders.standaloneSetup(new VehicleController(service, validator))
                .setValidator(validator).build();
    }

    @AfterEach
    void closeValidator() {
        validator.close();
    }

    @Test
    void readListsAllThreeVehicleTypes() throws Exception {
        List<Vehicle> vehicles = List.of(VehicleFactory.create("CAR"), VehicleFactory.create("VAN"), VehicleFactory.create("SUV"));
        when(service.search(null)).thenReturn(vehicles);
        mvc.perform(get("/vehicles")).andExpect(status().isOk()).andExpect(view().name("vehicle/list"))
                .andExpect(model().attribute("vehicles", vehicles));
    }

    @Test
    void searchRetainsKeywordAndMatchingToyotaVehicles() throws Exception {
        Vehicle car = car();
        car.setBrand("Toyota");
        car.setModel("Axio");
        Vehicle van = VehicleFactory.create("VAN");
        van.setBrand("Toyota");
        van.setModel("KDH");
        when(service.search("toyota")).thenReturn(List.of(car, van));
        mvc.perform(get("/vehicles").param("keyword", "toyota")).andExpect(status().isOk())
                .andExpect(model().attribute("vehicles", List.of(car, van)))
                .andExpect(model().attribute("keyword", "toyota"));
    }

    @Test
    void emptyResultsStillRenderTheListView() throws Exception {
        when(service.search("missing")).thenReturn(List.of());
        mvc.perform(get("/vehicles").param("keyword", "missing"))
                .andExpect(view().name("vehicle/list")).andExpect(model().attribute("vehicles", List.of()));
    }

    @Test
    void newFormHasDefaultsTypesStatusesAndCurrentYear() throws Exception {
        mvc.perform(get("/vehicles/new")).andExpect(view().name("vehicle/form"))
                .andExpect(model().attribute("editMode", false))
                .andExpect(model().attribute("vehicleTypes", VehicleFactory.TYPE_CODES))
                .andExpect(model().attribute("statuses", VehicleStatus.values()))
                .andExpect(model().attribute("currentYear", Year.now().getValue()))
                .andExpect(model().attribute("vehicleForm", hasProperty("status", is(VehicleStatus.AVAILABLE))));
    }

    @ParameterizedTest
    @CsvSource({"CAR, 4, Car", "VAN, 12, Van", "SUV, 7, SUV"})
    void createAcceptsEveryTypeWithinItsSeatLimit(String type, int seats, String label) throws Exception {
        MultiValueMap<String, String> params = validParams();
        params.set("vehicleType", type);
        params.set("seatingCapacity", Integer.toString(seats));
        Vehicle saved = VehicleFactory.create(type);
        saved.setRegistrationNumber("CAD-4321");
        when(service.create(any())).thenReturn(saved);
        mvc.perform(post("/vehicles").params(params)).andExpect(redirectedUrl("/vehicles"))
                .andExpect(flash().attribute("successMessage", label + " CAD-4321 added successfully."));
        verify(service).create(any(VehicleForm.class));
    }

    @ParameterizedTest
    @CsvSource({"CAR, 8, Car, 5", "VAN, 16, Van, 15", "SUV, 8, SUV, 7"})
    void rejectsSeatsAboveThePolymorphicLimit(String type, int seats, String name, int maxSeats) throws Exception {
        MultiValueMap<String, String> params = validParams();
        params.set("vehicleType", type);
        params.set("seatingCapacity", Integer.toString(seats));
        mvc.perform(post("/vehicles").params(params)).andExpect(view().name("vehicle/form"))
                .andExpect(model().attributeHasFieldErrors("vehicleForm", "seatingCapacity"))
                .andExpect(model().attribute("errors", hasEntry("seatingCapacity", "A " + name + " can have at most " + maxSeats + " seats")))
                .andExpect(model().attribute("vehicleForm", hasProperty("brand", is("Honda"))));
        verify(service, never()).create(any());
    }

    @Test
    void registrationFormatErrorIsDisplayedWithoutSaving() throws Exception {
        MultiValueMap<String, String> params = validParams();
        params.set("registrationNumber", "12345");
        mvc.perform(post("/vehicles").params(params)).andExpect(view().name("vehicle/form"))
                .andExpect(model().attribute("errors", hasEntry("registrationNumber", "Use the format ABC-1234 or AB-1234")));
        verify(service, never()).create(any());
        verify(service, never()).isRegistrationTaken(any(), any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"CAB-1234", "cab-1234"})
    void duplicateRegistrationsAreRejected(String registration) throws Exception {
        MultiValueMap<String, String> params = validParams();
        params.set("registrationNumber", registration);
        when(service.isRegistrationTaken(registration, null)).thenReturn(true);
        mvc.perform(post("/vehicles").params(params)).andExpect(view().name("vehicle/form"))
                .andExpect(model().attribute("errors", hasEntry("registrationNumber", "This registration number already exists")));
        verify(service, never()).create(any());
    }

    @Test
    void editPrefillsSavedValues() throws Exception {
        when(service.findById(1L)).thenReturn(Optional.of(car()));
        mvc.perform(get("/vehicles/1/edit")).andExpect(view().name("vehicle/form"))
                .andExpect(model().attribute("editMode", true)).andExpect(model().attribute("vehicleId", 1L))
                .andExpect(model().attribute("vehicleForm", hasProperty("registrationNumber", is("CAD-4321"))));
    }

    @Test
    void updateSavesMaintenanceAndExcludesCurrentRegistration() throws Exception {
        when(service.findById(1L)).thenReturn(Optional.of(car()));
        MultiValueMap<String, String> params = validParams();
        params.set("status", "MAINTENANCE");
        mvc.perform(post("/vehicles/1").params(params)).andExpect(redirectedUrl("/vehicles"))
                .andExpect(flash().attribute("successMessage", "Vehicle details updated."));
        verify(service).isRegistrationTaken("CAD-4321", 1L);
        verify(service).update(eq(1L), org.mockito.ArgumentMatchers.argThat(form -> form.getStatus() == VehicleStatus.MAINTENANCE));
    }

    @ParameterizedTest
    @ValueSource(strings = {"VAN", "", "INVALID"})
    void editingAlwaysValidatesAgainstTheSavedSubtype(String submittedType) throws Exception {
        when(service.findById(1L)).thenReturn(Optional.of(car()));
        MultiValueMap<String, String> params = validParams();
        params.set("vehicleType", submittedType);
        params.set("seatingCapacity", "8");
        mvc.perform(post("/vehicles/1").params(params)).andExpect(view().name("vehicle/form"))
                .andExpect(model().attribute("editMode", true))
                .andExpect(model().attribute("vehicleForm", hasProperty("vehicleType", is("CAR"))))
                .andExpect(model().attribute("errors", hasEntry("seatingCapacity", "A Car can have at most 5 seats")));
        verify(service, never()).update(any(), any());
    }

    @Test
    void updateDoesNotRequireTrustingTheHiddenTypeField() throws Exception {
        when(service.findById(1L)).thenReturn(Optional.of(car()));
        MultiValueMap<String, String> params = validParams();
        params.remove("vehicleType");
        mvc.perform(post("/vehicles/1").params(params)).andExpect(redirectedUrl("/vehicles"));
        verify(service).update(eq(1L), org.mockito.ArgumentMatchers.argThat(form -> "CAR".equals(form.getVehicleType())));
    }

    @Test
    void bookingConstraintDisplaysTheDocumentedDeleteError() throws Exception {
        when(service.findById(1L)).thenReturn(Optional.of(car()));
        doThrow(new DataIntegrityViolationException("booking foreign key")).when(service).delete(1L);
        mvc.perform(post("/vehicles/1/delete")).andExpect(redirectedUrl("/vehicles"))
                .andExpect(flash().attribute("errorMessage", "This vehicle is used in bookings, so it cannot be removed. Set its status to 'Under maintenance' instead."));
    }

    @Test
    void deleteUnreferencedVehicleDisplaysSuccess() throws Exception {
        when(service.findById(1L)).thenReturn(Optional.of(car()));
        mvc.perform(post("/vehicles/1/delete")).andExpect(redirectedUrl("/vehicles"))
                .andExpect(flash().attribute("successMessage", "Vehicle removed."));
        verify(service).delete(1L);
    }

    @Test
    void missingIdsRedirectWithAnErrorForEditUpdateAndDelete() throws Exception {
        when(service.findById(99L)).thenReturn(Optional.empty());
        mvc.perform(get("/vehicles/99/edit")).andExpect(redirectedUrl("/vehicles"))
                .andExpect(flash().attribute("errorMessage", "Vehicle not found."));
        mvc.perform(post("/vehicles/99").params(validParams())).andExpect(redirectedUrl("/vehicles"))
                .andExpect(flash().attribute("errorMessage", "Vehicle not found."));
        mvc.perform(post("/vehicles/99/delete")).andExpect(redirectedUrl("/vehicles"))
                .andExpect(flash().attribute("errorMessage", "Vehicle not found."));
        verify(service, never()).update(any(), any());
        verify(service, never()).delete(any());
    }

    @Test
    void blankRequiredFieldsAndInvalidTypeAreRejected() throws Exception {
        mvc.perform(post("/vehicles")).andExpect(view().name("vehicle/form"))
                .andExpect(model().attributeHasFieldErrors("vehicleForm", "vehicleType", "registrationNumber", "brand", "model", "manufactureYear", "seatingCapacity"));
        MultiValueMap<String, String> params = validParams();
        params.set("vehicleType", "MOTORCYCLE");
        mvc.perform(post("/vehicles").params(params)).andExpect(model().attributeHasFieldErrors("vehicleForm", "vehicleType"));
        verify(service, never()).create(any());
    }

    @Test
    void futureYearsAreRejectedUsingTheCurrentYear() throws Exception {
        MultiValueMap<String, String> params = validParams();
        params.set("manufactureYear", Integer.toString(Year.now().getValue() + 1));
        mvc.perform(post("/vehicles").params(params)).andExpect(view().name("vehicle/form"))
                .andExpect(model().attribute("errors", hasEntry("manufactureYear", "Manufacture year must not be in the future")));
    }

    @ParameterizedTest
    @CsvSource({"manufactureYear, abc, Please enter a whole number", "seatingCapacity, 3.5, Please enter a whole number", "status, INVALID, Please choose a valid status"})
    void bindingFailuresReturnReadableFieldErrors(String field, String value, String message) throws Exception {
        MultiValueMap<String, String> params = validParams();
        params.set(field, value);
        mvc.perform(post("/vehicles").params(params)).andExpect(view().name("vehicle/form"))
                .andExpect(model().attribute("errors", hasEntry(field, message)));
        verify(service, never()).create(any());
    }

    @Test
    void duplicateCreatedBetweenCheckAndSaveAlsoReturnsAFieldError() throws Exception {
        when(service.isRegistrationTaken("CAD-4321", null)).thenReturn(false, true);
        when(service.create(any())).thenThrow(new DataIntegrityViolationException("duplicate registration"));
        mvc.perform(post("/vehicles").params(validParams())).andExpect(view().name("vehicle/form"))
                .andExpect(model().attribute("errors", hasEntry("registrationNumber", "This registration number already exists")));
    }

    @Test
    void deletionRequiresPost() throws Exception {
        mvc.perform(get("/vehicles/1/delete")).andExpect(status().isMethodNotAllowed());
        verify(service, never()).delete(any());
    }

    private MultiValueMap<String, String> validParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("vehicleType", "CAR");
        params.add("registrationNumber", "CAD-4321");
        params.add("brand", "Honda");
        params.add("model", "Fit");
        params.add("manufactureYear", "2019");
        params.add("seatingCapacity", "4");
        params.add("status", "AVAILABLE");
        return params;
    }

    private Vehicle car() {
        Vehicle vehicle = new Car();
        vehicle.setVehicleId(1L);
        vehicle.setRegistrationNumber("CAD-4321");
        vehicle.setBrand("Honda");
        vehicle.setModel("Fit");
        vehicle.setManufactureYear(2019);
        vehicle.setSeatingCapacity(4);
        return vehicle;
    }
}
