<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${editMode ? 'Edit Fare Rule' : 'Add Fare Rule'}" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<c:set var="formAction" value="${editMode ? ('/admin/fares/' += fareRuleId) : '/admin/fares'}"/>

<div class="row justify-content-center">
    <div class="col-12 col-md-8 col-lg-6">
        <div class="card shadow-sm">
            <div class="card-body">
                <h3 class="card-title mb-4">${editMode ? 'Edit Fare Rule' : 'Add Fare Rule'}</h3>

                <form method="post" action="${formAction}" novalidate>

                    <div class="mb-3">
                        <label class="form-label" for="vehicleType">Vehicle type *</label>
                        <select id="vehicleType" name="vehicleType"
                                class="form-select ${not empty errors.vehicleType ? 'is-invalid' : ''}">
                            <option value="">-- Choose --</option>
                            <c:forEach var="type" items="${vehicleTypes}">
                                <option value="${type}" ${fareRule.vehicleType == type ? 'selected' : ''}>${type}</option>
                            </c:forEach>
                        </select>
                        <div class="invalid-feedback">${errors.vehicleType}</div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="baseFare">Base fare (Rs.) *</label>
                        <input type="number" id="baseFare" name="baseFare" step="0.01" min="0"
                               class="form-control ${not empty errors.baseFare ? 'is-invalid' : ''}"
                               value="${fareRule.baseFare}">
                        <div class="invalid-feedback">${errors.baseFare}</div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="pricePerKm">Price per km (Rs.) *</label>
                        <input type="number" id="pricePerKm" name="pricePerKm" step="0.01" min="0.01"
                               class="form-control ${not empty errors.pricePerKm ? 'is-invalid' : ''}"
                               value="${fareRule.pricePerKm}">
                        <div class="invalid-feedback">${errors.pricePerKm}</div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="minimumFare">Minimum fare (Rs.) *</label>
                        <input type="number" id="minimumFare" name="minimumFare" step="0.01" min="0"
                               class="form-control ${not empty errors.minimumFare ? 'is-invalid' : ''}"
                               value="${fareRule.minimumFare}">
                        <div class="invalid-feedback">${errors.minimumFare}</div>
                    </div>

                    <div class="form-check mb-3">
                        <input type="hidden" name="_active" value="on">
                        <input type="checkbox" id="active" name="active" class="form-check-input"
                               ${fareRule.active ? 'checked' : ''}>
                        <label class="form-check-label" for="active">Active (used for new bookings)</label>
                    </div>

                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-primary">${editMode ? 'Save Changes' : 'Add Fare Rule'}</button>
                        <a href="/admin/fares" class="btn btn-outline-secondary">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
