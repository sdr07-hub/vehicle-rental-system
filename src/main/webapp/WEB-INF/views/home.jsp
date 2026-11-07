<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Home" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h1 class="text-center mb-2">Welcome</h1>
<p class="text-center text-muted mb-5">Choose how you want to continue</p>

<div class="row g-4">
    <div class="col-12 col-md-4">
        <div class="card h-100 text-center shadow-sm">
            <div class="card-body">
                <h5 class="card-title">Customer</h5>
                <p class="card-text">Register, book a vehicle and share feedback.</p>
                <a href="/customers" class="btn btn-primary">Go to Customer</a>
            </div>
        </div>
    </div>
    <div class="col-12 col-md-4">
        <div class="card h-100 text-center shadow-sm">
            <div class="card-body">
                <h5 class="card-title">Driver</h5>
                <p class="card-text">Manage your licence, availability and assigned trips.</p>
                <a href="/drivers" class="btn btn-success">Go to Driver</a>
            </div>
        </div>
    </div>
    <div class="col-12 col-md-4">
        <div class="card h-100 text-center shadow-sm">
            <div class="card-body">
                <h5 class="card-title">Admin</h5>
                <p class="card-text">Manage vehicles, bookings, fares and feedback.</p>
                <a href="/admin" class="btn btn-dark">Go to Admin</a>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
