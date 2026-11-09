<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="My dashboard" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<%-- Welcome banner --%>
<section class="welcome-banner mb-4">
    <div>
        <p class="text-uppercase small fw-semibold mb-1 opacity-75">Customer dashboard</p>
        <h1 class="h2 fw-bold mb-1">Hi <c:out value="${sessionScope.currentUser.firstName}"/>, where to today?</h1>
        <p class="mb-0 opacity-75">Pick a vehicle type below to book your ride.</p>
    </div>
    <a href="/customer/book" class="btn btn-primary btn-lg"><i class="bi bi-plus-circle"></i> Book a ride</a>
</section>

<%-- Three small numbers --%>
<div class="row g-3 mb-5">
    <div class="col-12 col-md-4">
        <div class="stat-card">
            <div class="stat-icon"><i class="bi bi-calendar-check"></i></div>
            <div><div class="stat-value">${rideCount}</div><div class="stat-label">Rides booked</div></div>
        </div>
    </div>
    <div class="col-12 col-md-4">
        <div class="stat-card">
            <div class="stat-icon"><i class="bi bi-hourglass-split"></i></div>
            <div><div class="stat-value">${activeRides}</div><div class="stat-label">Active rides</div></div>
        </div>
    </div>
    <div class="col-12 col-md-4">
        <div class="stat-card">
            <div class="stat-icon"><i class="bi bi-cash-coin"></i></div>
            <div><div class="stat-value">Rs. ${totalSpent}</div><div class="stat-label">Spent on finished rides</div></div>
        </div>
    </div>
</div>

<%-- Vehicle types --%>
<div class="page-header">
    <h2 class="h4 fw-bold mb-0">Choose your ride</h2>
</div>

<c:choose>
    <c:when test="${empty vehicleTypes}">
        <div class="alert alert-info">No vehicle types are available yet. Please check back later.</div>
    </c:when>
    <c:otherwise>
        <div class="row g-4 mb-5">
            <c:forEach var="t" items="${vehicleTypes}">
                <div class="col-12 col-md-6 col-lg-4">
                    <div class="vehicle-card h-100">
                        <div class="vehicle-img">
                            <i class="bi bi-car-front-fill vehicle-img-fallback"></i>
                            <img src="/images/${t.imageFile}" alt="${t.displayName}" onerror="this.style.display='none'">
                            <span class="vehicle-seats"><i class="bi bi-people-fill"></i> ${t.seats} seats</span>
                        </div>
                        <div class="p-3 d-flex flex-column flex-grow-1">
                            <h3 class="h5 fw-bold mb-1">${t.displayName}</h3>
                            <p class="text-muted small mb-3">${t.tagline}</p>
                            <ul class="price-list mb-3">
                                <li><span>Base fare</span><strong>Rs. ${t.baseFare}</strong></li>
                                <li><span>Per km</span><strong>Rs. ${t.pricePerKm}</strong></li>
                                <li><span>Minimum fare</span><strong>Rs. ${t.minimumFare}</strong></li>
                            </ul>
                            <c:choose>
                                <c:when test="${t.bookable}">
                                    <a href="/customer/book?type=${t.vehicleType}" class="btn btn-primary mt-auto">Book ${t.displayName}</a>
                                </c:when>
                                <c:otherwise>
                                    <button class="btn btn-outline-secondary mt-auto" disabled>Not available now</button>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<%-- Recent rides --%>
<div class="page-header">
    <h2 class="h4 fw-bold mb-0">Recent rides</h2>
    <a href="/customer/rides" class="btn btn-outline-primary btn-sm">See all rides</a>
</div>

<c:choose>
    <c:when test="${empty recentRides}">
        <div class="empty-state">
            <i class="bi bi-signpost-split"></i>
            <p class="mb-0">No rides yet. Your booked rides will appear here.</p>
        </div>
    </c:when>
    <c:otherwise>
        <div class="list-group shadow-sm">
            <c:forEach var="r" items="${recentRides}">
                <a href="/customer/rides" class="list-group-item list-group-item-action py-3">
                    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
                        <div>
                            <div class="fw-semibold"><c:out value="${r.pickupLocation}"/> &rarr; <c:out value="${r.dropLocation}"/></div>
                            <small class="text-muted">${r.pickupTime} &middot; <c:out value="${r.vehicleLabel}"/></small>
                        </div>
                        <div class="text-end">
                            <span class="badge ${statusMap[r.status].badgeClass}">${statusMap[r.status].label}</span>
                            <div class="fw-semibold">Rs. ${r.estimatedFare}</div>
                        </div>
                    </div>
                </a>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
