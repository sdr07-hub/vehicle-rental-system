<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Welcome" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section class="hero text-center text-white rounded-4 mb-5">
    <h1 class="display-5 fw-bold">Vehicle Rental System</h1>
    <p class="lead mb-0">Rent a vehicle, book a driver, manage your trips.</p>
</section>

<h2 class="text-center h4 mb-4">Who are you?</h2>

<div class="row g-4 justify-content-center">

    <div class="col-12 col-md-4">
        <a href="/login?role=CUSTOMER" class="role-card role-customer">
            <img src="/images/customer.jpg" alt="Customer" class="role-img">
            <div class="role-body">
                <h3 class="h5">I'm a Customer</h3>
                <p class="text-muted">Book vehicles, manage your trips and share feedback.</p>
                <span class="btn btn-primary btn-sm">Continue &rarr;</span>
            </div>
        </a>
    </div>

    <div class="col-12 col-md-4">
        <a href="/login?role=DRIVER" class="role-card role-driver">
            <img src="/images/driver.jpg" alt="Driver" class="role-img">
            <div class="role-body">
                <h3 class="h5">I'm a Driver</h3>
                <p class="text-muted">Set your availability and see your assigned trips.</p>
                <span class="btn btn-success btn-sm">Continue &rarr;</span>
            </div>
        </a>
    </div>

    <div class="col-12 col-md-4">
        <a href="/login?role=ADMIN" class="role-card role-admin">
            <img src="/images/admin.jpg" alt="Administrator" class="role-img">
            <div class="role-body">
                <h3 class="h5">I'm an Administrator</h3>
                <p class="text-muted">Manage vehicles, bookings, fares and feedback.</p>
                <span class="btn btn-dark btn-sm">Continue &rarr;</span>
            </div>
        </a>
    </div>

</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>