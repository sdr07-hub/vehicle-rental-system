<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="pageTitle" value="${editMode ? 'Edit Customer' : 'Register Customer'}" scope="request"/>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<%-- The same form is used for CREATE and UPDATE. Only the address it sends to changes. --%>

<c:choose>
    <c:when test="${editMode}">
        <c:url var="formAction" value="/customers/${customerId}"/>
    </c:when>
    <c:otherwise>
        <c:url var="formAction" value="/customers"/>
    </c:otherwise>
</c:choose>

<div class="row justify-content-center">

    <div class="col-12 col-md-8 col-lg-6">

        <div class="card shadow-sm">

            <div class="card-body">

                <h3 class="card-title mb-4">${editMode ? 'Edit Customer' : 'Register Customer'}</h3>

                <form method="post" action="${formAction}" novalidate>

                    <div class="mb-3">

                        <label class="form-label" for="fullName">Full name *</label>

                        <input type="text" id="fullName" name="fullName" maxlength="100"

                               class="form-control ${not empty errors.fullName ? 'is-invalid' : ''}"

                               value="${fn:escapeXml(customer.fullName)}">

                        <div class="invalid-feedback">${errors.fullName}</div>

                    </div>

                    <div class="mb-3">

                        <label class="form-label" for="email">Email *</label>

                        <input type="email" id="email" name="email" maxlength="100"

                               class="form-control ${not empty errors.email ? 'is-invalid' : ''}"

                               value="${fn:escapeXml(customer.email)}">

                        <div class="invalid-feedback">${errors.email}</div>

                    </div>

                    <div class="mb-3">

                        <label class="form-label" for="phone">Phone number *</label>

                        <input type="tel" id="phone" name="phone" maxlength="10" placeholder="0771234567"

                               class="form-control ${not empty errors.phone ? 'is-invalid' : ''}"

                               value="${fn:escapeXml(customer.phone)}">

                        <div class="invalid-feedback">${errors.phone}</div>

                    </div>

                    <div class="mb-3">

                        <label class="form-label" for="password">

                            ${editMode ? 'New password (leave empty to keep the current one)' : 'Password *'}

                        </label>

                        <%-- The value is never filled in: passwords are never sent back to the browser --%>

                        <input type="password" id="password" name="password"

                               class="form-control ${not empty errors.password ? 'is-invalid' : ''}">

                        <div class="invalid-feedback">${errors.password}</div>

                        <div class="form-hint">At least 6 characters.</div>

                    </div>

                    <div class="mb-3">

                        <label class="form-label" for="address">Address</label>

                        <input type="text" id="address" name="address" maxlength="255"

                               class="form-control ${not empty errors.address ? 'is-invalid' : ''}"

                               value="${fn:escapeXml(customer.address)}">

                        <div class="invalid-feedback">${errors.address}</div>

                    </div>

                    <div class="mb-3">

                        <label class="form-label" for="dateOfBirth">Date of birth</label>

                        <input type="date" id="dateOfBirth" name="dateOfBirth"

                               class="form-control ${not empty errors.dateOfBirth ? 'is-invalid' : ''}"

                               value="${customer.dateOfBirth}">

                        <div class="invalid-feedback">${errors.dateOfBirth}</div>

                    </div>

                    <c:if test="${editMode}">

                        <div class="form-check mb-3">

                                <%-- "_active" tells Spring to set active=false when the box is NOT ticked --%>

                            <input type="hidden" name="_active" value="on">

                            <input type="checkbox" id="active" name="active" class="form-check-input"

                                ${customer.active ? 'checked' : ''}>

                            <label class="form-check-label" for="active">Active (untick to deactivate)</label>

                        </div>

                    </c:if>

                    <div class="d-flex gap-2">

                        <button type="submit" class="btn btn-primary">

                            ${editMode ? 'Save Changes' : 'Register'}

                        </button>

                        <a href="/customers" class="btn btn-outline-secondary">Cancel</a>

                    </div>

                </form>

            </div>

        </div>

    </div>

</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>