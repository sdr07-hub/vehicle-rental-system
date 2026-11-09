<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Sign up" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="auth-card card border-0 shadow-lg mx-auto">
    <div class="row g-0">

        <div class="col-lg-5 d-none d-lg-block auth-side">
            <div class="auth-side-content">
                <h2 class="fw-bold">Ride with us</h2>
                <ul class="list-unstyled mb-0 auth-points">
                    <li><i class="bi bi-check-circle-fill"></i> Cars, vans and SUVs</li>
                    <li><i class="bi bi-check-circle-fill"></i> See the fare before you book</li>
                    <li><i class="bi bi-check-circle-fill"></i> Track every ride in one place</li>
                </ul>
            </div>
        </div>

        <div class="col-lg-7">
            <div class="p-4 p-md-5">
                <h1 class="h3 fw-bold mb-1">Create your account</h1>
                <p class="text-muted mb-4">Fields marked * are required.</p>

                <form method="post" action="/signup" novalidate>
                    <div class="mb-3">
                        <label class="form-label" for="fullName">Full name *</label>
                        <input type="text" id="fullName" name="fullName" maxlength="100"
                               class="form-control ${not empty errors.fullName ? 'is-invalid' : ''}"
                               value="${fn:escapeXml(customer.fullName)}">
                        <div class="invalid-feedback">${errors.fullName}</div>
                    </div>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label" for="email">Email *</label>
                            <input type="email" id="email" name="email" maxlength="100"
                                   class="form-control ${not empty errors.email ? 'is-invalid' : ''}"
                                   value="${fn:escapeXml(customer.email)}">
                            <div class="invalid-feedback">${errors.email}</div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label" for="phone">Phone number *</label>
                            <input type="tel" id="phone" name="phone" maxlength="10" placeholder="0771234567"
                                   class="form-control ${not empty errors.phone ? 'is-invalid' : ''}"
                                   value="${fn:escapeXml(customer.phone)}">
                            <div class="invalid-feedback">${errors.phone}</div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label" for="password">Password *</label>
                            <input type="password" id="password" name="password"
                                   class="form-control ${not empty errors.password ? 'is-invalid' : ''}">
                            <div class="invalid-feedback">${errors.password}</div>
                            <div class="form-hint">At least 6 characters.</div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label" for="confirmPassword">Confirm password *</label>
                            <input type="password" id="confirmPassword" name="confirmPassword"
                                   class="form-control ${not empty errors.confirmPassword ? 'is-invalid' : ''}">
                            <div class="invalid-feedback">${errors.confirmPassword}</div>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="address">Address</label>
                        <input type="text" id="address" name="address" maxlength="255"
                               class="form-control ${not empty errors.address ? 'is-invalid' : ''}"
                               value="${fn:escapeXml(customer.address)}">
                        <div class="invalid-feedback">${errors.address}</div>
                    </div>

                    <div class="mb-4">
                        <label class="form-label" for="dateOfBirth">Date of birth</label>
                        <input type="date" id="dateOfBirth" name="dateOfBirth"
                               class="form-control ${not empty errors.dateOfBirth ? 'is-invalid' : ''}"
                               value="${customer.dateOfBirth}">
                        <div class="invalid-feedback">${errors.dateOfBirth}</div>
                    </div>

                    <button type="submit" class="btn btn-primary btn-lg w-100">Sign up</button>
                </form>

                <p class="text-center mt-4 mb-0">
                    Already have an account? <a href="/login" class="fw-semibold">Log in</a>
                </p>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
