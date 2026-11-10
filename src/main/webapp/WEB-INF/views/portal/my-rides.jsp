<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="My rides" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="page-header">
    <div>
        <h1 class="h3 fw-bold mb-0">My rides</h1>
        <p class="text-muted mb-0">Every ride you have booked, newest first.</p>
    </div>
    <a href="/customer/book" class="btn btn-primary"><i class="bi bi-plus-circle"></i> Book a ride</a>
</div>

<c:choose>
    <c:when test="${empty rides}">
        <div class="empty-state">
            <i class="bi bi-signpost-split"></i>
            <p>You have not booked any rides yet.</p>
            <a href="/customer/book" class="btn btn-primary">Book your first ride</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="row g-3">
            <c:forEach var="r" items="${rides}">
                <div class="col-12 col-lg-6">
                    <div class="ride-card status-${r.status}">
                        <div class="d-flex justify-content-between align-items-start mb-2">
                            <span class="text-muted small">Ride #${r.bookingId}</span>
                            <span class="badge ${statusMap[r.status].badgeClass}">${statusMap[r.status].label}</span>
                        </div>

                        <div class="route mb-2">
                            <div><i class="bi bi-geo-alt-fill text-success"></i> <c:out value="${r.pickupLocation}"/></div>
                            <div><i class="bi bi-flag-fill text-danger"></i> <c:out value="${r.dropLocation}"/></div>
                        </div>

                        <div class="small text-muted mb-3">
                            <i class="bi bi-clock"></i> ${r.pickupTime} &middot; ${r.distanceKm} km<br>
                            <i class="bi bi-car-front"></i> <c:out value="${r.vehicleLabel}"/><br>
                            <i class="bi bi-person-badge"></i>
                            <c:choose>
                                <c:when test="${empty r.driverName}">Waiting for a driver to accept</c:when>
                                <c:otherwise>Driver: <c:out value="${r.driverName}"/></c:otherwise>
                            </c:choose>
                        </div>

                        <div class="d-flex justify-content-between align-items-center">
                            <c:choose>
                                <c:when test="${r.status == 'COMPLETED'}">
                                    <div><span class="small text-muted d-block">Total fare</span>
                                        <span class="ride-total">Rs. ${r.estimatedFare}</span></div>
                                </c:when>
                                <c:otherwise>
                                    <div><span class="small text-muted d-block">Estimated fare</span>
                                        <span class="fw-bold">Rs. ${r.estimatedFare}</span></div>
                                </c:otherwise>
                            </c:choose>

                            <c:if test="${r.status == 'COMPLETED'}">
                                <a href="/customer/feedback/new?rideId=${r.bookingId}" class="btn btn-sm btn-outline-primary">
                                    <i class="bi bi-star"></i> Rate ride
                                </a>
                            </c:if>
                            <c:if test="${r.status == 'PENDING' or r.status == 'CONFIRMED'}">
                                <form method="post" action="/customer/rides/${r.bookingId}/cancel"
                                      onsubmit="return confirm('Cancel this ride?');">
                                    <button type="submit" class="btn btn-sm btn-outline-danger">Cancel ride</button>
                                </form>
                            </c:if>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
