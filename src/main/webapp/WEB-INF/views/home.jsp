<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Home" scope="request"/>
<c:set var="fullWidth" value="${true}" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<%-- HERO: big photo (images/hero.jpg) with a dark overlay --%>
<section class="hero">
    <div class="container">
        <div class="row">
            <div class="col-lg-7">
                <span class="hero-pill"><i class="bi bi-lightning-charge-fill"></i> Cars, vans and SUVs</span>
                <h1 class="display-4 fw-bold mt-3">Your ride, ready when you are.</h1>
                <p class="lead mb-4 opacity-75">Book a vehicle in a minute, see the fare before you go, and pay only for the distance you travel.</p>
                <c:choose>
                    <c:when test="${empty sessionScope.currentUser}">
                        <div class="d-flex flex-wrap gap-2">
                            <a href="/signup" class="btn btn-primary btn-lg px-4">Create an account</a>
                            <a href="/login" class="btn btn-outline-light btn-lg px-4">Log in</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <a href="${sessionScope.currentUser.homeUrl}" class="btn btn-primary btn-lg px-4">Go to my dashboard</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</section>

<%-- VEHICLE TYPES --%>
<section class="container py-5">
    <div class="text-center mb-4">
        <h2 class="fw-bold">Choose the right vehicle</h2>
        <p class="text-muted">Every type has its own fare: a base fare plus a price per kilometre.</p>
    </div>
    <div class="row g-4">
        <div class="col-12 col-md-4">
            <div class="vehicle-card h-100">
                <div class="vehicle-img">
                    <i class="bi bi-car-front-fill vehicle-img-fallback"></i>
                    <img src="/images/car.jpg" alt="Car" onerror="this.style.display='none'">
                    <span class="vehicle-seats"><i class="bi bi-people-fill"></i> 4 seats</span>
                </div>
                <div class="p-3"><h3 class="h5 fw-bold mb-1">Car</h3><p class="text-muted small mb-0">Everyday rides around the city.</p></div>
            </div>
        </div>
        <div class="col-12 col-md-4">
            <div class="vehicle-card h-100">
                <div class="vehicle-img">
                    <i class="bi bi-bus-front-fill vehicle-img-fallback"></i>
                    <img src="/images/van.jpg" alt="Van" onerror="this.style.display='none'">
                    <span class="vehicle-seats"><i class="bi bi-people-fill"></i> 12 seats</span>
                </div>
                <div class="p-3"><h3 class="h5 fw-bold mb-1">Van</h3><p class="text-muted small mb-0">Groups, families and airport trips.</p></div>
            </div>
        </div>
        <div class="col-12 col-md-4">
            <div class="vehicle-card h-100">
                <div class="vehicle-img">
                    <i class="bi bi-truck-front-fill vehicle-img-fallback"></i>
                    <img src="/images/suv.jpg" alt="SUV" onerror="this.style.display='none'">
                    <span class="vehicle-seats"><i class="bi bi-people-fill"></i> 7 seats</span>
                </div>
                <div class="p-3"><h3 class="h5 fw-bold mb-1">SUV</h3><p class="text-muted small mb-0">Space and comfort for long journeys.</p></div>
            </div>
        </div>
    </div>
</section>

<%-- HOW IT WORKS --%>
<section class="how-it-works py-5">
    <div class="container">
        <h2 class="fw-bold text-center mb-4">How it works</h2>
        <div class="row g-4 text-center">
            <div class="col-12 col-md-4">
                <div class="step-circle">1</div>
                <h3 class="h6 fw-bold">Book</h3>
                <p class="text-muted small mb-0">Pick a vehicle type, enter pickup, drop-off and distance, and see the fare.</p>
            </div>
            <div class="col-12 col-md-4">
                <div class="step-circle">2</div>
                <h3 class="h6 fw-bold">A driver accepts</h3>
                <p class="text-muted small mb-0">A nearby driver accepts your request and comes to the pickup point.</p>
            </div>
            <div class="col-12 col-md-4">
                <div class="step-circle">3</div>
                <h3 class="h6 fw-bold">Ride and pay</h3>
                <p class="text-muted small mb-0">When the ride finishes, the total fare is shown to you and the driver.</p>
            </div>
        </div>
    </div>
</section>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
