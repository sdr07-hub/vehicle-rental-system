<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Login" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="row justify-content-center">
<div class="col-12 col-md-6 col-lg-4">
<div class="card shadow-sm">
<div class="card-body">

<h3 class="card-title mb-4">Login</h3>

<form method="post" action="/login" novalidate>

<div class="mb-3">
<label class="form-label" for="email">Email</label>
<input type="email" id="email" name="email" class="form-control" required>
</div>

<div class="mb-3">
<label class="form-label" for="password">Password</label>
<input type="password" id="password" name="password" class="form-control" required>
</div>

<button type="submit" class="btn btn-primary w-100">Log in</button>

</form>

<p class="text-muted small mt-3 mb-0">Sample password for all test users: password123</p>

</div>
</div>
</div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>