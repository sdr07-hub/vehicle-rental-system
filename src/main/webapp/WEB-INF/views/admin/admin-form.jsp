<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="${editMode ? 'Edit Admin' : 'Add Admin'}" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<c:set var="formAction" value="${editMode ? ('/admin/admins/' += adminId) : '/admin/admins'}"/>

<div class="row justify-content-center">
    <div class="col-12 col-md-8 col-lg-6">
        <div class="card shadow-sm">
            <div class="card-body">
                <h3 class="card-title mb-4">${editMode ? 'Edit Admin' : 'Add Admin'}</h3>

                <form method="post" action="${formAction}" novalidate>

                    <div class="mb-3">
                        <label class="form-label" for="fullName">Full name *</label>
                        <input type="text" id="fullName" name="fullName" maxlength="100"
                               class="form-control ${not empty errors.fullName ? 'is-invalid' : ''}"
                               value="${fn:escapeXml(admin.fullName)}">
                        <div class="invalid-feedback">${errors.fullName}</div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="email">Email *</label>
                        <input type="email" id="email" name="email" maxlength="100"
                               class="form-control ${not empty errors.email ? 'is-invalid' : ''}"
                               value="${fn:escapeXml(admin.email)}">
                        <div class="invalid-feedback">${errors.email}</div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="phone">Phone number *</label>
                        <input type="tel" id="phone" name="phone" maxlength="10" placeholder="0771234567"
                               class="form-control ${not empty errors.phone ? 'is-invalid' : ''}"
                               value="${fn:escapeXml(admin.phone)}">
                        <div class="invalid-feedback">${errors.phone}</div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="password">
                            ${editMode ? 'New password (leave empty to keep the current one)' : 'Password *'}
                        </label>
                        <input type="password" id="password" name="password"
                               class="form-control ${not empty errors.password ? 'is-invalid' : ''}">
                        <div class="invalid-feedback">${errors.password}</div>
                        <div class="form-hint">At least 8 characters.</div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="adminLevel">Admin level *</label>
                        <select id="adminLevel" name="adminLevel"
                                class="form-select ${not empty errors.adminLevel ? 'is-invalid' : ''}">
                            <c:forEach var="level" items="${levels}">
                                <option value="${level}" ${admin.adminLevel == level ? 'selected' : ''}>${level.label}</option>
                            </c:forEach>
                        </select>
                        <div class="invalid-feedback">${errors.adminLevel}</div>
                    </div>

                    <c:if test="${editMode}">
                        <div class="form-check mb-3">
                            <input type="hidden" name="_active" value="on">
                            <input type="checkbox" id="active" name="active" class="form-check-input"
                                   ${admin.active ? 'checked' : ''}>
                            <label class="form-check-label" for="active">Active</label>
                        </div>
                    </c:if>

                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-dark">${editMode ? 'Save Changes' : 'Add Admin'}</button>
                        <a href="/admin/admins" class="btn btn-outline-secondary">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
