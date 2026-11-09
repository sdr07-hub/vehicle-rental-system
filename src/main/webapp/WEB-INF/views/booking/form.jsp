<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="${editMode ? 'Edit Booking' : 'New Booking'}" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<c:set var="formAction" value="${editMode ? ('/bookings/' += bookingId) : '/bookings'}"/>

<div class="row justify-content-center">
    <div class="col-12 col-md-10 col-lg-7">
        <div class="card shadow-sm">
            <div class="card-body">
                <h3 class="card-title mb-4">${editMode ? ('Edit Booking #' += bookingId) : 'New Booking'}</h3>

                <form method="post" action="${formAction}" novalidate>

                    <div class="mb-3">
                        <label class="form-label" for="customerId">Customer *</label>
                        <select id="customerId" name="customerId"
                                class="form-select ${not empty errors.customerId ? 'is-invalid' : ''}">
                            <option value="">-- Choose a customer --</option>
                            <c:forEach var="o" items="${customerOptions}">
                                <option value="${o.id}" ${booking.customerId == o.id ? 'selected' : ''}><c:out value="${o.label}"/></option>
                            </c:forEach>
                        </select>
                        <div class="invalid-feedback">${errors.customerId}</div>
                    </div>

                    <div class="row">
                        <div class="col-12 col-md-6 mb-3">
                            <label class="form-label" for="pickupLocation">Pickup location *</label>
                            <input type="text" id="pickupLocation" name="pickupLocation" maxlength="150"
                                   class="form-control ${not empty errors.pickupLocation ? 'is-invalid' : ''}"
                                   value="${fn:escapeXml(booking.pickupLocation)}">
                            <div class="invalid-feedback">${errors.pickupLocation}</div>
                        </div>
                        <div class="col-12 col-md-6 mb-3">
                            <label class="form-label" for="dropLocation">Drop location *</label>
                            <input type="text" id="dropLocation" name="dropLocation" maxlength="150"
                                   class="form-control ${not empty errors.dropLocation ? 'is-invalid' : ''}"
                                   value="${fn:escapeXml(booking.dropLocation)}">
                            <div class="invalid-feedback">${errors.dropLocation}</div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-12 col-md-6 mb-3">
                            <label class="form-label" for="pickupTime">Pickup date and time *</label>
                            <input type="datetime-local" id="pickupTime" name="pickupTime"
                                   class="form-control ${not empty errors.pickupTime ? 'is-invalid' : ''}"
                                   value="${booking.pickupTime}">
                            <div class="invalid-feedback">${errors.pickupTime}</div>
                            <div class="form-hint">7-9 AM and 5-7 PM are peak hours (+20%).</div>
                        </div>
                        <div class="col-12 col-md-6 mb-3">
                            <label class="form-label" for="distanceKm">Distance (km) *</label>
                            <input type="number" id="distanceKm" name="distanceKm" step="0.1" min="0.5"
                                   class="form-control ${not empty errors.distanceKm ? 'is-invalid' : ''}"
                                   value="${booking.distanceKm}">
                            <div class="invalid-feedback">${errors.distanceKm}</div>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="vehicleId">Vehicle *</label>
                        <select id="vehicleId" name="vehicleId"
                                class="form-select ${not empty errors.vehicleId ? 'is-invalid' : ''}">
                            <option value="">-- Choose a vehicle --</option>
                            <c:forEach var="o" items="${vehicleOptions}">
                                <option value="${o.id}" ${booking.vehicleId == o.id ? 'selected' : ''}><c:out value="${o.label}"/></option>
                            </c:forEach>
                        </select>
                        <div class="invalid-feedback">${errors.vehicleId}</div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="driverId">Driver</label>
                        <select id="driverId" name="driverId"
                                class="form-select ${not empty errors.driverId ? 'is-invalid' : ''}">
                            <option value="">-- Assign later --</option>
                            <c:forEach var="o" items="${driverOptions}">
                                <option value="${o.id}" ${booking.driverId == o.id ? 'selected' : ''}><c:out value="${o.label}"/></option>
                            </c:forEach>
                        </select>
                        <div class="invalid-feedback">${errors.driverId}</div>
                        <%-- AMENDMENT: explains how this choice shows up in the driver dashboard --%>
                        <div class="form-hint">Choosing a driver confirms the booking and puts it in that driver's dashboard. "Assign later" shows it to all drivers as a new request.</div>
                    </div>

                    <c:if test="${editMode}">
                        <div class="mb-3">
                            <label class="form-label" for="status">Status *</label>
                            <select id="status" name="status"
                                    class="form-select ${not empty errors.status ? 'is-invalid' : ''}">
                                <c:forEach var="s" items="${statuses}">
                                    <option value="${s}" ${booking.status == s ? 'selected' : ''}>${s.label}</option>
                                </c:forEach>
                            </select>
                            <div class="invalid-feedback">${errors.status}</div>
                            <div class="form-hint">Pending &rarr; Confirmed &rarr; In progress &rarr; Completed. Cancel only while Pending or Confirmed. Moving back to Pending removes the driver.</div>
                        </div>
                    </c:if>

                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-primary">${editMode ? 'Save Changes' : 'Create Booking'}</button>
                        <a href="/bookings" class="btn btn-outline-secondary">Back</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
