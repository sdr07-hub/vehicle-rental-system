<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Bookings" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="page-header">
    <div>
        <h2 class="mb-0">Bookings</h2>
        <%-- AMENDMENT: explains how this admin page works with the customer and driver dashboards --%>
        <p class="text-muted mb-0">Rides booked by customers in the app appear here too. Pending rides without a driver are shown to drivers as new requests.</p>
    </div>
    <a href="/bookings/new" class="btn btn-primary">+ New Booking</a>
</div>

<%-- Filter by status and search by customer or place --%>
<form method="get" action="/bookings" class="row g-2 mb-3">
    <div class="col-12 col-md-3">
        <select name="status" class="form-select">
            <option value="">All statuses</option>
            <c:forEach var="s" items="${statuses}">
                <option value="${s}" ${selectedStatus == s.name() ? 'selected' : ''}>${s.label}</option>
            </c:forEach>
        </select>
    </div>
    <div class="col-12 col-md-5">
        <input type="text" name="keyword" class="form-control"
               placeholder="Customer name, pickup or drop location" value="${fn:escapeXml(keyword)}">
    </div>
    <div class="col-auto">
        <button type="submit" class="btn btn-outline-primary">Filter</button>
        <a href="/bookings" class="btn btn-outline-secondary">Clear</a>
    </div>
</form>

<c:choose>
    <c:when test="${empty bookings}">
        <div class="alert alert-info">No bookings found.</div>
    </c:when>
    <c:otherwise>
        <div class="table-responsive">
            <table class="table table-striped table-hover bg-white shadow-sm">
                <thead class="table-dark">
                <tr>
                    <th>#</th>
                    <th>Customer</th>
                    <th>Trip</th>
                    <th>Pickup time</th>
                    <th>Vehicle</th>
                    <th>Driver</th>
                    <th>Fare (Rs.)</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="b" items="${bookings}">
                    <tr>
                        <td>${b.bookingId}</td>
                        <td><c:out value="${b.customerName}"/></td>
                        <td>
                            <c:out value="${b.pickupLocation}"/> &rarr; <c:out value="${b.dropLocation}"/><br>
                            <small class="text-muted">${b.distanceKm} km</small>
                        </td>
                        <td>${b.pickupTime}</td>
                        <td><c:out value="${b.vehicleLabel}"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${empty b.driverName}"><span class="text-muted">Not assigned</span></c:when>
                                <c:otherwise><c:out value="${b.driverName}"/></c:otherwise>
                            </c:choose>
                        </td>
                        <td>${b.estimatedFare}</td>
                        <td>
                            <%-- Find the matching BookingStatus to show its label and colour --%>
                            <c:forEach var="s" items="${statuses}">
                                <c:if test="${s.name() == b.status}">
                                    <span class="badge ${s.badgeClass}">${s.label}</span>
                                </c:if>
                            </c:forEach>
                        </td>
                        <td class="action-buttons">
                            <c:if test="${b.status != 'COMPLETED' and b.status != 'CANCELLED'}">
                                <a href="/bookings/${b.bookingId}/edit" class="btn btn-sm btn-warning">Edit</a>
                            </c:if>
                            <c:if test="${b.status == 'PENDING' or b.status == 'CONFIRMED'}">
                                <form method="post" action="/bookings/${b.bookingId}/cancel" class="d-inline"
                                      onsubmit="return confirm('Cancel this booking?');">
                                    <button type="submit" class="btn btn-sm btn-outline-danger">Cancel</button>
                                </form>
                            </c:if>
                            <form method="post" action="/bookings/${b.bookingId}/delete" class="d-inline"
                                  onsubmit="return confirm('Delete this booking permanently?');">
                                <button type="submit" class="btn btn-sm btn-danger">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <p class="text-muted">Total: ${fn:length(bookings)} booking(s)</p>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
