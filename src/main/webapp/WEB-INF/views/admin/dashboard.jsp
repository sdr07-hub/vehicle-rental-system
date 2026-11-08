<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Admin Dashboard" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h2 class="mb-4">Admin Dashboard</h2>

<%-- Numbers come from AdminService.getDashboardStats() --%>
<div class="row g-3 mb-4">
    <div class="col-6 col-md-3">
        <div class="card text-center shadow-sm"><div class="card-body">
            <div class="fs-2 fw-bold">${stats.customers}</div><div class="text-muted">Customers</div>
        </div></div>
    </div>
    <div class="col-6 col-md-3">
        <div class="card text-center shadow-sm"><div class="card-body">
            <div class="fs-2 fw-bold">${stats.drivers}</div><div class="text-muted">Drivers</div>
        </div></div>
    </div>
    <div class="col-6 col-md-3">
        <div class="card text-center shadow-sm"><div class="card-body">
            <div class="fs-2 fw-bold">${stats.availableVehicles} / ${stats.vehicles}</div>
            <div class="text-muted">Vehicles available</div>
        </div></div>
    </div>
    <div class="col-6 col-md-3">
        <div class="card text-center shadow-sm"><div class="card-body">
            <div class="fs-2 fw-bold">${stats.pendingBookings} / ${stats.bookings}</div>
            <div class="text-muted">Bookings pending</div>
        </div></div>
    </div>
    <div class="col-6 col-md-3">
        <div class="card text-center shadow-sm"><div class="card-body">
            <div class="fs-2 fw-bold">${stats.openFeedback}</div><div class="text-muted">Open feedback</div>
        </div></div>
    </div>
    <div class="col-6 col-md-3">
        <div class="card text-center shadow-sm"><div class="card-body">
            <div class="fs-2 fw-bold">${stats.admins}</div><div class="text-muted">Admins</div>
        </div></div>
    </div>
</div>

<h5 class="mb-3">Manage</h5>
<div class="list-group shadow-sm">
    <a href="/admin/fares" class="list-group-item list-group-item-action">Fare rules</a>
    <a href="/admin/fares/estimate" class="list-group-item list-group-item-action">Fare calculator</a>
    <a href="/admin/admins" class="list-group-item list-group-item-action">Admin accounts</a>
    <a href="/customers" class="list-group-item list-group-item-action">Customers</a>
    <a href="/drivers" class="list-group-item list-group-item-action">Drivers</a>
    <a href="/vehicles" class="list-group-item list-group-item-action">Vehicles</a>
    <a href="/bookings" class="list-group-item list-group-item-action">Bookings</a>
    <a href="/feedback" class="list-group-item list-group-item-action">Feedback and complaints</a>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
