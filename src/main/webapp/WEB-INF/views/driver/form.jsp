<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="${editMode ? 'Edit Driver' : 'Register Driver'}" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<c:set var="formAction" value="${editMode ? ('/drivers/' += driverId) : '/drivers'}"/>

<div class="row justify-content-center">
    <div class="col-12 col-md-8 col-lg-6">
        <div class="card shadow-sm">
            <div class="card-body">
                <h3 class="card-title mb-4">${editMode ? 'Edit Driver' : 'Register Driver'}</h3>

                <form method="post" action="${formAction}" novalidate>

                    <h6 class="text-muted">Personal details</h6>

                    <div class="mb-3">
                        <label class="form-label" for="fullName">Full name *</label>
                        <input type="text" id="fullName" name="fullName" maxlength="100"
                               class="form-control ${not empty errors.fullName ? 'is-invalid' : ''}"
                               value="${fn:escapeXml(driver.fullName)}">
                        <div class="invalid-feedback">${errors.fullName}</div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="email">Email *</label>
                        <input type="email" id="email" name="email" maxlength="100"
                               class="form-control ${not empty errors.email ? 'is-invalid' : ''}"
                               value="${fn:escapeXml(driver.email)}">
                        <div class="invalid-feedback">${errors.email}</div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="phone">Phone number *</label>
                        <input type="tel" id="phone" name="phone" maxlength="10" placeholder="0771234567"
                               class="form-control ${not empty errors.phone ? 'is-invalid' : ''}"
                               value="${fn:escapeXml(driver.phone)}">
                        <div class="invalid-feedback">${errors.phone}</div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="password">
                            ${editMode ? 'New password (leave empty to keep the current one)' : 'Password *'}
                        </label>
                        <input type="password" id="password" name="password"
                               class="form-control ${not empty errors.password ? 'is-invalid' : ''}">
                        <div class="invalid-feedback">${errors.password}</div>
                        <div class="form-hint">At least 6 characters.</div>
                    </div>

                    <h6 class="text-muted mt-4">Licence and availability</h6>

                    <div class="mb-3">
                        <label class="form-label" for="licenceNumber">Licence number *</label>
                        <input type="text" id="licenceNumber" name="licenceNumber" maxlength="15" placeholder="B1234567"
                               class="form-control ${not empty errors.licenceNumber ? 'is-invalid' : ''}"
                               value="${fn:escapeXml(driver.licenceNumber)}">
                        <div class="invalid-feedback">${errors.licenceNumber}</div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="licenceExpiry">Licence expiry date *</label>
                        <input type="date" id="licenceExpiry" name="licenceExpiry"
                               class="form-control ${not empty errors.licenceExpiry ? 'is-invalid' : ''}"
                               value="${driver.licenceExpiry}">
                        <div class="invalid-feedback">${errors.licenceExpiry}</div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="yearsOfExperience">Years of experience *</label>
                        <input type="number" id="yearsOfExperience" name="yearsOfExperience" min="0" max="60"
                               class="form-control ${not empty errors.yearsOfExperience ? 'is-invalid' : ''}"
                               value="${driver.yearsOfExperience}">
                        <div class="invalid-feedback">${errors.yearsOfExperience}</div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="availability">Availability *</label>
                        <select id="availability" name="availability"
                                class="form-select ${not empty errors.availability ? 'is-invalid' : ''}">
                            <c:forEach var="option" items="${availabilities}">
                                <option value="${option}" ${driver.availability == option ? 'selected' : ''}>${option.label}</option>
                            </c:forEach>
                        </select>
                        <div class="invalid-feedback">${errors.availability}</div>
                    </div>

                    <c:if test="${editMode}">
                        <div class="form-check mb-3">
                            <input type="hidden" name="_active" value="on">
                            <input type="checkbox" id="active" name="active" class="form-check-input"
                                   ${driver.active ? 'checked' : ''}>
                            <label class="form-check-label" for="active">Active (untick to deactivate)</label>
                        </div>
                    </c:if>

                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-success">${editMode ? 'Save Changes' : 'Register'}</button>
                        <a href="/drivers" class="btn btn-outline-secondary">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
