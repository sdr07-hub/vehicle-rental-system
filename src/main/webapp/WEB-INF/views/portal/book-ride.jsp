<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Book a ride" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="page-header">
    <div>
        <h1 class="h3 fw-bold mb-0">Book a ride</h1>
        <p class="text-muted mb-0">Choose a vehicle type, enter your trip and see the fare before you book.</p>
    </div>
    <a href="/customer/dashboard" class="btn btn-outline-secondary"><i class="bi bi-arrow-left"></i> Dashboard</a>
</div>

<form method="post" action="/customer/book" novalidate>
    <div class="row g-4">

        <%-- LEFT: the form --%>
        <div class="col-12 col-lg-8">

            <%-- 1. Vehicle type as picture cards (radio buttons) --%>
            <div class="card border-0 shadow-sm mb-4">
                <div class="card-body p-4">
                    <h2 class="h5 fw-bold mb-3">1. Vehicle type</h2>
                    <div class="row g-3">
                        <c:forEach var="t" items="${vehicleTypes}">
                            <div class="col-12 col-md-4">
                                <input type="radio" class="btn-check type-radio" name="vehicleType"
                                       id="type-${t.vehicleType}" value="${t.vehicleType}"
                                       data-base="${t.baseFare}" data-perkm="${t.pricePerKm}" data-min="${t.minimumFare}"
                                       data-name="${t.displayName}"
                                       ${ride.vehicleType == t.vehicleType ? 'checked' : ''}
                                       ${t.bookable ? '' : 'disabled'}>
                                <label class="type-option" for="type-${t.vehicleType}">
                                    <span class="type-img">
                                        <i class="bi bi-car-front-fill vehicle-img-fallback"></i>
                                        <img src="/images/${t.imageFile}" alt="${t.displayName}" onerror="this.style.display='none'">
                                    </span>
                                    <span class="fw-bold d-block">${t.displayName}</span>
                                    <span class="small text-muted d-block">${t.seats} seats &middot; Rs. ${t.pricePerKm}/km</span>
                                    <c:if test="${not t.bookable}"><span class="small text-danger">Not available</span></c:if>
                                </label>
                            </div>
                        </c:forEach>
                    </div>
                    <c:if test="${not empty errors.vehicleType}">
                        <div class="text-danger small mt-2">${errors.vehicleType}</div>
                    </c:if>
                </div>
            </div>

            <%-- 2. Trip details --%>
            <div class="card border-0 shadow-sm">
                <div class="card-body p-4">
                    <h2 class="h5 fw-bold mb-3">2. Trip details</h2>

                    <div class="row">
                        <div class="col-12 col-md-6 mb-3">
                            <label class="form-label" for="pickupLocation">Pickup location *</label>
                            <div class="input-group has-validation">
                                <span class="input-group-text"><i class="bi bi-geo-alt-fill text-success"></i></span>
                                <input type="text" id="pickupLocation" name="pickupLocation" maxlength="150"
                                       placeholder="e.g. Colombo Fort"
                                       class="form-control ${not empty errors.pickupLocation ? 'is-invalid' : ''}"
                                       value="${fn:escapeXml(ride.pickupLocation)}">
                                <div class="invalid-feedback">${errors.pickupLocation}</div>
                            </div>
                        </div>
                        <div class="col-12 col-md-6 mb-3">
                            <label class="form-label" for="dropLocation">Drop location *</label>
                            <div class="input-group has-validation">
                                <span class="input-group-text"><i class="bi bi-flag-fill text-danger"></i></span>
                                <input type="text" id="dropLocation" name="dropLocation" maxlength="150"
                                       placeholder="e.g. Bambalapitiya"
                                       class="form-control ${not empty errors.dropLocation ? 'is-invalid' : ''}"
                                       value="${fn:escapeXml(ride.dropLocation)}">
                                <div class="invalid-feedback">${errors.dropLocation}</div>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-12 col-md-6 mb-3">
                            <label class="form-label" for="pickupTime">Pickup date and time *</label>
                            <input type="datetime-local" id="pickupTime" name="pickupTime"
                                   class="form-control ${not empty errors.pickupTime ? 'is-invalid' : ''}"
                                   value="${ride.pickupTime}">
                            <div class="invalid-feedback">${errors.pickupTime}</div>
                            <div class="form-hint">7-9 AM and 5-7 PM are peak hours (+20%).</div>
                        </div>
                        <div class="col-12 col-md-6 mb-3">
                            <label class="form-label" for="distanceKm">Distance (km) *</label>
                            <input type="number" id="distanceKm" name="distanceKm" step="0.1" min="0.5"
                                   class="form-control ${not empty errors.distanceKm ? 'is-invalid' : ''}"
                                   value="${ride.distanceKm}">
                            <div class="invalid-feedback">${errors.distanceKm}</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <%-- RIGHT: live fare estimate --%>
        <div class="col-12 col-lg-4">
            <div class="fare-card sticky-lg-top">
                <p class="text-uppercase small fw-semibold mb-1 opacity-75">Estimated fare</p>
                <div class="fare-amount" id="fareAmount">Rs. --</div>
                <p class="small mb-3 opacity-75" id="fareNote">Choose a vehicle type and enter the distance.</p>
                <ul class="list-unstyled small mb-4 fare-breakdown">
                    <li><span>Vehicle</span><strong id="fareVehicle">-</strong></li>
                    <li><span>Pricing</span><strong id="farePricing">Standard</strong></li>
                </ul>
                <button type="submit" class="btn btn-light btn-lg w-100 fw-semibold">Confirm booking</button>
                <p class="small mt-3 mb-0 opacity-75">The final fare is checked again when you book. A driver accepts your ride next.</p>
            </div>
        </div>
    </div>
</form>

<%-- Same formula as BookingRepository.calculateStandardFare + PeakHourPricing:
     max(base + perKm x km, minimum), then +20% between 7-9 AM and 5-7 PM. --%>
<script>
    (function () {
        const radios = document.querySelectorAll('.type-radio');
        const distance = document.getElementById('distanceKm');
        const time = document.getElementById('pickupTime');

        function update() {
            const chosen = document.querySelector('.type-radio:checked');
            const km = parseFloat(distance.value);
            let peak = false;
            if (time.value) {
                const hour = new Date(time.value).getHours();
                peak = (hour >= 7 && hour < 9) || (hour >= 17 && hour < 19);
            }
            document.getElementById('farePricing').textContent = peak ? 'Peak hour (+20%)' : 'Standard';
            document.getElementById('fareVehicle').textContent = chosen ? chosen.dataset.name : '-';

            if (!chosen || isNaN(km) || km <= 0) {
                document.getElementById('fareAmount').textContent = 'Rs. --';
                document.getElementById('fareNote').textContent = 'Choose a vehicle type and enter the distance.';
                return;
            }
            const base = parseFloat(chosen.dataset.base);
            const perKm = parseFloat(chosen.dataset.perkm);
            const min = parseFloat(chosen.dataset.min);
            let fare = Math.max(base + perKm * km, min);
            if (peak) { fare = fare * 1.2; }
            document.getElementById('fareAmount').textContent = 'Rs. ' + fare.toFixed(2);
            document.getElementById('fareNote').textContent =
                'Rs. ' + base.toFixed(2) + ' base + Rs. ' + perKm.toFixed(2) + ' x ' + km + ' km';
        }

        radios.forEach(r => r.addEventListener('change', update));
        distance.addEventListener('input', update);
        time.addEventListener('input', update);
        update();
    })();
</script>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
