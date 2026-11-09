<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Log in" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="auth-card card border-0 shadow-lg mx-auto">
    <div class="row g-0">

        <%-- Left: photo panel (hidden on phones) --%>
        <div class="col-lg-5 d-none d-lg-block auth-side">
            <div class="auth-side-content">
                <h2 class="fw-bold">Welcome back</h2>
                <p class="mb-0">Customers book rides, drivers accept them, and admins keep everything running &mdash; all from one login.</p>
            </div>
        </div>

        <%-- Right: the form --%>
        <div class="col-lg-7">
            <div class="p-4 p-md-5">
                <h1 class="h3 fw-bold mb-1">Log in</h1>
                <p class="text-muted mb-4">Use the email and password of your account.</p>

                <c:if test="${param.required != null}">
                    <div class="alert alert-warning py-2">Please log in to continue.</div>
                </c:if>

                <form method="post" action="/login" novalidate>
                    <div class="mb-3">
                        <label class="form-label" for="email">Email</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                            <input type="email" id="email" name="email" class="form-control" required autofocus
                                   placeholder="you@example.com" value="${fn:escapeXml(email)}">
                        </div>
                    </div>

                    <div class="mb-4">
                        <label class="form-label" for="password">Password</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-lock"></i></span>
                            <input type="password" id="password" name="password" class="form-control" required>
                        </div>
                    </div>

                    <button type="submit" class="btn btn-primary btn-lg w-100">Log in</button>
                </form>

                <p class="text-center mt-4 mb-0">
                    New here? <a href="/signup" class="fw-semibold">Create a customer account</a>
                </p>

                <%-- Demo accounts from sample-data.sql. Remove this box before the final submission. --%>
                <div class="demo-box mt-4">
                    <div class="fw-semibold mb-1"><i class="bi bi-info-circle"></i> Demo accounts (password: password123)</div>
                    <div>Customer: nimal@example.com</div>
                    <div>Driver: ruwan@example.com</div>
                    <div>Admin: admin@example.com</div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>