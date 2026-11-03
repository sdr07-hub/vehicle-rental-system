<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="pageTitle" value="${editMode ? 'Edit Vehicle' : 'Add Vehicle'}" scope="request" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<c:url var="vehiclesUrl" value="/vehicles" />
<c:choose>
    <c:when test="${editMode}"><c:url var="formAction" value="/vehicles/${vehicleId}" /></c:when>
    <c:otherwise><c:url var="formAction" value="/vehicles" /></c:otherwise>
</c:choose>

<section class="container py-4" aria-labelledby="vehicleFormHeading">
    <div class="row justify-content-center">
        <div class="col-12 col-lg-9">
            <div class="card shadow-sm">
                <div class="card-body p-4">
                    <h1 id="vehicleFormHeading" class="h3 mb-4"><c:out value="${pageTitle}" /></h1>
                    <c:if test="${not empty errors or not empty globalErrors}">
                        <div class="alert alert-danger" role="alert">
                            Please check the form and try again.
                            <c:forEach var="error" items="${globalErrors}">
                                <div><c:out value="${error.defaultMessage}" /></div>
                            </c:forEach>
                        </div>
                    </c:if>

                    <form:form modelAttribute="vehicleForm" action="${formAction}" method="post" htmlEscape="true">
                        <div class="row g-3">
                            <div class="col-12 col-md-6">
                                <label for="vehicleType" class="form-label">Vehicle type <span aria-hidden="true">*</span></label>
                                <c:choose>
                                    <c:when test="${editMode}">
                                        <input id="vehicleType" class="form-control" type="text" readonly
                                               value="${fn:escapeXml(vehicleForm.vehicleType)}" aria-describedby="vehicleTypeHint">
                                        <form:hidden path="vehicleType" id="savedVehicleType" />
                                    </c:when>
                                    <c:otherwise>
                                        <form:select path="vehicleType" id="vehicleType" required="required"
                                                     cssClass="form-select" cssErrorClass="form-select is-invalid"
                                                     aria-describedby="vehicleTypeHint vehicleTypeError">
                                            <form:option value="" label="Choose a vehicle type" />
                                            <c:forEach var="type" items="${vehicleTypes}">
                                                <form:option value="${type}" label="${type}" />
                                            </c:forEach>
                                        </form:select>
                                    </c:otherwise>
                                </c:choose>
                                <div id="vehicleTypeHint" class="form-text">Seat limits: Car 5, Van 15, SUV 7.</div>
                                <c:if test="${not empty errors.vehicleType}">
                                    <div id="vehicleTypeError" class="invalid-feedback d-block"><c:out value="${errors.vehicleType}" /></div>
                                </c:if>
                            </div>

                            <div class="col-12 col-md-6">
                                <label for="registrationNumber" class="form-label">Registration number <span aria-hidden="true">*</span></label>
                                <form:input path="registrationNumber" id="registrationNumber" required="required" maxlength="8"
                                            placeholder="CAB-1234" cssClass="form-control" cssErrorClass="form-control is-invalid"
                                            aria-describedby="registrationNumberError" />
                                <c:if test="${not empty errors.registrationNumber}">
                                    <div id="registrationNumberError" class="invalid-feedback d-block"><c:out value="${errors.registrationNumber}" /></div>
                                </c:if>
                            </div>

                            <div class="col-12 col-md-6">
                                <label for="brand" class="form-label">Brand <span aria-hidden="true">*</span></label>
                                <form:input path="brand" id="brand" required="required" maxlength="50" placeholder="Toyota"
                                            cssClass="form-control" cssErrorClass="form-control is-invalid" aria-describedby="brandError" />
                                <c:if test="${not empty errors.brand}">
                                    <div id="brandError" class="invalid-feedback d-block"><c:out value="${errors.brand}" /></div>
                                </c:if>
                            </div>

                            <div class="col-12 col-md-6">
                                <label for="model" class="form-label">Model <span aria-hidden="true">*</span></label>
                                <form:input path="model" id="model" required="required" maxlength="50" placeholder="Axio"
                                            cssClass="form-control" cssErrorClass="form-control is-invalid" aria-describedby="modelError" />
                                <c:if test="${not empty errors.model}">
                                    <div id="modelError" class="invalid-feedback d-block"><c:out value="${errors.model}" /></div>
                                </c:if>
                            </div>

                            <div class="col-12 col-md-6">
                                <label for="manufactureYear" class="form-label">Manufacture year <span aria-hidden="true">*</span></label>
                                <form:input path="manufactureYear" id="manufactureYear" type="number" required="required"
                                            min="1990" max="${currentYear}" step="1" cssClass="form-control"
                                            cssErrorClass="form-control is-invalid" aria-describedby="manufactureYearError" />
                                <c:if test="${not empty errors.manufactureYear}">
                                    <div id="manufactureYearError" class="invalid-feedback d-block"><c:out value="${errors.manufactureYear}" /></div>
                                </c:if>
                            </div>

                            <div class="col-12 col-md-6">
                                <label for="seatingCapacity" class="form-label">Seating capacity <span aria-hidden="true">*</span></label>
                                <form:input path="seatingCapacity" id="seatingCapacity" type="number" required="required"
                                            min="1" max="50" step="1" cssClass="form-control"
                                            cssErrorClass="form-control is-invalid" aria-describedby="seatingCapacityError" />
                                <c:if test="${not empty errors.seatingCapacity}">
                                    <div id="seatingCapacityError" class="invalid-feedback d-block"><c:out value="${errors.seatingCapacity}" /></div>
                                </c:if>
                            </div>

                            <div class="col-12 col-md-6">
                                <label for="colour" class="form-label">Colour <span class="text-muted">(optional)</span></label>
                                <form:input path="colour" id="colour" maxlength="30" cssClass="form-control"
                                            cssErrorClass="form-control is-invalid" aria-describedby="colourError" />
                                <c:if test="${not empty errors.colour}">
                                    <div id="colourError" class="invalid-feedback d-block"><c:out value="${errors.colour}" /></div>
                                </c:if>
                            </div>

                            <div class="col-12 col-md-6">
                                <label for="status" class="form-label">Status <span aria-hidden="true">*</span></label>
                                <form:select path="status" id="status" required="required" cssClass="form-select"
                                             cssErrorClass="form-select is-invalid" aria-describedby="statusError">
                                    <form:option value="" label="Choose a status" />
                                    <c:forEach var="vehicleStatus" items="${statuses}">
                                        <form:option value="${vehicleStatus}" label="${vehicleStatus.label}" />
                                    </c:forEach>
                                </form:select>
                                <c:if test="${not empty errors.status}">
                                    <div id="statusError" class="invalid-feedback d-block"><c:out value="${errors.status}" /></div>
                                </c:if>
                            </div>
                        </div>
                        <div class="d-flex flex-wrap gap-2 mt-4">
                            <button type="submit" class="btn btn-primary"><c:out value="${editMode ? 'Save Changes' : 'Add Vehicle'}" /></button>
                            <a href="${fn:escapeXml(vehiclesUrl)}" class="btn btn-outline-secondary">Cancel</a>
                        </div>
                    </form:form>
                </div>
            </div>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
