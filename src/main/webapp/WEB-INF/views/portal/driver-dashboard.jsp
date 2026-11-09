<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Driver dashboard" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section class="welcome-banner mb-4">
    <div>
        <p class="text-uppercase small fw-semibold mb-1 opacity-75">Driver dashboard</p>
        <h1 class="h2 fw-bold mb-1">Hi <c:out value="${sessionScope.currentUser.firstName}"/>, ready to drive?</h1>
        <p class="mb-0 opacity-75">Accept a ride request, start it when the customer is in, and finish it at the drop-off.</p>
    </div>
</section>

<div class="row g-3 mb-5">
    <div class="col-12 col-md-4">
        <div class="stat-card">
            <div class="stat-icon"><i class="bi bi-bell"></i></div>
            <div><div class="stat-value">${openRequests.size()}</div><div class="stat-label">New ride requests</div></div>
        </div>
    </div>
    <div class="col-12 col-md-4">
        <div class="stat-card">
            <div class="stat-icon"><i class="bi bi-flag"></i></div>
            <div><div class="stat-value">${completedCount}</div><div class="stat-label">Rides completed</div></div>
        </div>
    </div>
    <div class="col-12 col-md-4">
        <div class="stat-card">
            <div class="stat-icon"><i class="bi bi-cash-stack"></i></div>
            <div><div class="stat-value">Rs. ${earnings}</div><div class="stat-label">Total fares collected</div></div>
        </div>
    </div>
</div>

<%-- New ride requests --%>
<div class="page-header">
    <h2 class="h4 fw-bold mb-0">New ride requests</h2>
</div>

<c:choose>
    <c:when test="${empty openRequests}">
        <div class="empty-state mb-5">
            <i class="bi bi-cup-hot"></i>
            <p class="mb-0">No new requests right now. This page shows them as soon as customers book.</p>
        </div>
    </c:when>
    <c:otherwise>
        <div class="row g-3 mb-5">
            <c:forEach var="r" items="${openRequests}">
                <div class="col-12 col-lg-6">
                    <div class="ride-card status-PENDING">
                        <div class="d-flex justify-content-between mb-2">
                            <span class="fw-semibold"><c:out value="${r.customerName}"/></span>
                            <span class="text-muted small">Ride #${r.bookingId}</span>
                        </div>
                        <div class="route mb-2">
                            <div><i class="bi bi-geo-alt-fill text-success"></i> <c:out value="${r.pickupLocation}"/></div>
                            <div><i class="bi bi-flag-fill text-danger"></i> <c:out value="${r.dropLocation}"/></div>
                        </div>
                        <div class="small text-muted mb-3">
                            <i class="bi bi-clock"></i> ${r.pickupTime} &middot; ${r.distanceKm} km &middot; <c:out value="${r.vehicleLabel}"/>
                        </div>
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="fw-bold">Rs. ${r.estimatedFare}</span>
                            <form method="post" action="/driver/rides/${r.bookingId}/accept">
                                <button type="submit" class="btn btn-success"><i class="bi bi-check-lg"></i> Accept ride</button>
                            </form>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<%-- My rides --%>
<div class="page-header">
    <h2 class="h4 fw-bold mb-0">My rides</h2>
    <span class="text-muted small">${activeCount} active</span>
</div>

<c:choose>
    <c:when test="${empty myRides}">
        <div class="empty-state">
            <i class="bi bi-signpost-split"></i>
            <p class="mb-0">Rides you accept will appear here.</p>
        </div>
    </c:when>
    <c:otherwise>
        <div class="row g-3">
            <c:forEach var="r" items="${myRides}">
                <div class="col-12 col-lg-6">
                    <div class="ride-card status-${r.status}">
                        <div class="d-flex justify-content-between align-items-start mb-2">
                            <span class="fw-semibold"><c:out value="${r.customerName}"/></span>
                            <span class="badge ${statusMap[r.status].badgeClass}">${statusMap[r.status].label}</span>
                        </div>
                        <div class="route mb-2">
                            <div><i class="bi bi-geo-alt-fill text-success"></i> <c:out value="${r.pickupLocation}"/></div>
                            <div><i class="bi bi-flag-fill text-danger"></i> <c:out value="${r.dropLocation}"/></div>
                        </div>
                        <div class="small text-muted mb-3">
                            <i class="bi bi-clock"></i> ${r.pickupTime} &middot; ${r.distanceKm} km &middot; Ride #${r.bookingId}
                        </div>
                        <div class="d-flex justify-content-between align-items-center">
                            <c:choose>
                                <c:when test="${r.status == 'COMPLETED'}">
                                    <div><span class="small text-muted d-block">Total fare</span>
                                        <span class="ride-total">Rs. ${r.estimatedFare}</span></div>
                                </c:when>
                                <c:otherwise>
                                    <span class="fw-bold">Rs. ${r.estimatedFare}</span>
                                </c:otherwise>
                            </c:choose>

                            <c:if test="${r.status == 'CONFIRMED'}">
                                <form method="post" action="/driver/rides/${r.bookingId}/start">
                                    <button type="submit" class="btn btn-warning"><i class="bi bi-play-fill"></i> Start ride</button>
                                </form>
                            </c:if>
                            <c:if test="${r.status == 'IN_PROGRESS'}">
                                <form method="post" action="/driver/rides/${r.bookingId}/finish"
                                      onsubmit="return confirm('Finish this ride?');">
                                    <button type="submit" class="btn btn-primary"><i class="bi bi-flag-fill"></i> Finish ride</button>
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