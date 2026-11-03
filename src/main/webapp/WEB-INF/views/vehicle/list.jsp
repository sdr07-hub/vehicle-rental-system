<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Vehicles" scope="request" />
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<c:url var="vehiclesUrl" value="/vehicles" />
<c:url var="newVehicleUrl" value="/vehicles/new" />
<section class="container py-4" aria-labelledby="vehiclesHeading">
    <div class="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-4">
        <h1 id="vehiclesHeading" class="mb-0">Vehicles</h1>
        <a href="${fn:escapeXml(newVehicleUrl)}" class="btn btn-primary">+ Add Vehicle</a>
    </div>

    <c:if test="${not empty successMessage}">
        <div class="alert alert-success" role="status"><c:out value="${successMessage}" /></div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger" role="alert"><c:out value="${errorMessage}" /></div>
    </c:if>

    <form action="${fn:escapeXml(vehiclesUrl)}" method="get" class="row g-2 mb-4" role="search">
        <div class="col-12 col-md">
            <label for="keyword" class="visually-hidden">Search vehicles</label>
            <input id="keyword" name="keyword" type="search" class="form-control"
                   value="${fn:escapeXml(keyword)}" placeholder="Search by registration, brand or model">
        </div>
        <div class="col-auto"><button type="submit" class="btn btn-primary">Search</button></div>
        <div class="col-auto"><a href="${fn:escapeXml(vehiclesUrl)}" class="btn btn-outline-secondary">Clear</a></div>
    </form>

    <c:choose>
        <c:when test="${empty vehicles}">
            <div class="alert alert-info" role="status">No vehicles found.</div>
        </c:when>
        <c:otherwise>
            <div class="table-responsive">
                <table class="table table-striped table-hover align-middle">
                    <caption class="visually-hidden">Vehicles matching the current search</caption>
                    <thead class="table-dark">
                    <tr>
                        <th scope="col">ID</th>
                        <th scope="col">Type</th>
                        <th scope="col">Registration</th>
                        <th scope="col">Vehicle</th>
                        <th scope="col">Year</th>
                        <th scope="col">Seats</th>
                        <th scope="col">Status</th>
                        <th scope="col">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="vehicle" items="${vehicles}">
                        <c:url var="editUrl" value="/vehicles/${vehicle.vehicleId}/edit" />
                        <c:url var="deleteUrl" value="/vehicles/${vehicle.vehicleId}/delete" />
                        <tr>
                            <td><c:out value="${vehicle.vehicleId}" /></td>
                            <td><c:out value="${vehicle.typeName}" /></td>
                            <td class="text-nowrap"><c:out value="${vehicle.registrationNumber}" /></td>
                            <td>
                                <div class="fw-semibold"><c:out value="${vehicle.brand}" /> <c:out value="${vehicle.model}" /></div>
                                <c:if test="${not empty vehicle.colour}">
                                    <div class="small text-muted"><c:out value="${vehicle.colour}" /></div>
                                </c:if>
                                <div class="small text-muted"><c:out value="${vehicle.description}" /></div>
                            </td>
                            <td><c:out value="${vehicle.manufactureYear}" /></td>
                            <td class="text-nowrap"><c:out value="${vehicle.seatingCapacity}" /> / <c:out value="${vehicle.maxSeats}" /></td>
                            <td><span class="badge ${fn:escapeXml(vehicle.status.badgeClass)}"><c:out value="${vehicle.status.label}" /></span></td>
                            <td>
                                <div class="d-flex gap-2">
                                    <a href="${fn:escapeXml(editUrl)}" class="btn btn-sm btn-outline-primary"
                                       aria-label="Edit ${fn:escapeXml(vehicle.registrationNumber)}">Edit</a>
                                    <form action="${fn:escapeXml(deleteUrl)}" method="post"
                                          onsubmit="return confirm('Remove this vehicle? This action cannot be undone.');">
                                        <c:if test="${not empty _csrf}">
                                            <input type="hidden" name="${fn:escapeXml(_csrf.parameterName)}" value="${fn:escapeXml(_csrf.token)}">
                                        </c:if>
                                        <button type="submit" class="btn btn-sm btn-outline-danger"
                                                aria-label="Delete ${fn:escapeXml(vehicle.registrationNumber)}">Delete</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
    <p class="text-muted mt-3">Total vehicles: <c:out value="${fn:length(vehicles)}" /></p>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
