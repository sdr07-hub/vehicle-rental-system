<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Fare Calculator" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="row justify-content-center">
    <div class="col-12 col-md-8 col-lg-6">
        <div class="card shadow-sm">
            <div class="card-body">
                <h3 class="card-title mb-4">Fare Calculator</h3>

                <form method="get" action="/admin/fares/estimate">
                    <div class="mb-3">
                        <label class="form-label" for="vehicleType">Vehicle type</label>
                        <select id="vehicleType" name="vehicleType" class="form-select">
                            <c:forEach var="type" items="${vehicleTypes}">
                                <option value="${type}" ${vehicleType == type ? 'selected' : ''}>${type}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="distanceKm">Distance (km)</label>
                        <input type="number" id="distanceKm" name="distanceKm" step="0.1" min="0.1"
                               class="form-control" value="${distanceKm}" required>
                    </div>
                    <div class="form-check mb-3">
                        <input type="checkbox" id="night" name="night" class="form-check-input" ${night ? 'checked' : ''}>
                        <label class="form-check-label" for="night">Night trip (+25%)</label>
                    </div>
                    <button type="submit" class="btn btn-primary">Calculate</button>
                    <a href="/admin/fares" class="btn btn-outline-secondary">Back to fare rules</a>
                </form>

                <c:if test="${not empty fare}">
                    <div class="alert alert-success mt-4 mb-0">
                        <div class="fs-4 fw-bold">Rs. ${fare}</div>
                        <div class="small">Calculated by: <c:out value="${calculatorName}"/></div>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
