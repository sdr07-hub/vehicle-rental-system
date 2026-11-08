<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="My Trips" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<%-- Mobile-friendly page: cards instead of a wide table --%>
<div class="row justify-content-center">
    <div class="col-12 col-md-8 col-lg-6">

        <div class="card shadow-sm mb-3">
            <div class="card-body">
                <h4 class="mb-1"><c:out value="${driver.fullName}"/></h4>
                <p class="text-muted mb-2"><c:out value="${driver.profileSummary}"/></p>
                <p class="mb-2">
                    Status: <span class="badge ${driver.availability.badgeClass}">${driver.availability.label}</span>
                    <c:if test="${not driver.licenceValid}">
                        <span class="badge bg-danger">Licence expired</span>
                    </c:if>
                </p>

                <%-- Quick availability change (UPDATE) --%>
                <form method="post" action="/drivers/${driver.userId}/availability" class="d-flex gap-2">
                    <select name="availability" class="form-select">
                        <c:forEach var="option" items="${availabilities}">
                            <option value="${option}" ${driver.availability == option ? 'selected' : ''}>${option.label}</option>
                        </c:forEach>
                    </select>
                    <button type="submit" class="btn btn-primary">Update</button>
                </form>
            </div>
        </div>

        <h5 class="mb-3">Assigned trips</h5>

        <c:if test="${empty trips}">
            <div class="alert alert-info">No trips assigned yet.</div>
        </c:if>

        <c:forEach var="trip" items="${trips}">
            <div class="card shadow-sm mb-3 trip-card">
                <div class="card-body">
                    <div class="d-flex justify-content-between">
                        <strong>Booking #${trip.bookingId}</strong>
                        <span class="badge bg-primary">${trip.status}</span>
                    </div>
                    <div class="mt-2">
                        <div><strong>From:</strong> <c:out value="${trip.pickupLocation}"/></div>
                        <div><strong>To:</strong> <c:out value="${trip.dropLocation}"/></div>
                        <div><strong>Pickup:</strong> ${trip.pickupTime}</div>
                        <div><strong>Customer:</strong> <c:out value="${trip.customerName}"/>
                            (<a href="tel:${trip.customerPhone}"><c:out value="${trip.customerPhone}"/></a>)</div>
                        <div><strong>Fare:</strong> Rs. ${trip.estimatedFare}</div>
                    </div>
                </div>
            </div>
        </c:forEach>

        <a href="/drivers" class="btn btn-outline-secondary">Back to drivers</a>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
