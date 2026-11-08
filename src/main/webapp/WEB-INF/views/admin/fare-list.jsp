<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Fare Rules" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="page-header">
    <h2 class="mb-0">Fare Rules</h2>
    <div class="d-flex gap-2">
        <a href="/admin" class="btn btn-outline-secondary">Dashboard</a>
        <a href="/admin/fares/estimate" class="btn btn-outline-primary">Fare calculator</a>
        <a href="/admin/fares/new" class="btn btn-primary">+ Add Fare Rule</a>
    </div>
</div>

<p class="text-muted">Fare = base fare + (price per km &times; distance). It is never less than the minimum fare.</p>

<c:choose>
    <c:when test="${empty fareRules}">
        <div class="alert alert-info">No fare rules yet.</div>
    </c:when>
    <c:otherwise>
        <div class="table-responsive">
            <table class="table table-striped table-hover bg-white shadow-sm">
                <thead class="table-dark">
                <tr>
                    <th>Vehicle type</th>
                    <th>Base fare (Rs.)</th>
                    <th>Per km (Rs.)</th>
                    <th>Minimum (Rs.)</th>
                    <th>Example: 10 km</th>
                    <th>Active</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="rule" items="${fareRules}">
                    <tr>
                        <td><span class="badge bg-dark">${rule.vehicleType}</span></td>
                        <td>${rule.baseFare}</td>
                        <td>${rule.pricePerKm}</td>
                        <td>${rule.minimumFare}</td>
                        <%-- Calls the Java method calculateFare(10) on each rule --%>
                        <td>Rs. ${rule.calculateFare(10)}</td>
                        <td>${rule.active ? 'Yes' : 'No'}</td>
                        <td class="action-buttons">
                            <a href="/admin/fares/${rule.fareRuleId}/edit" class="btn btn-sm btn-warning">Edit</a>
                            <form method="post" action="/admin/fares/${rule.fareRuleId}/delete" class="d-inline"
                                  onsubmit="return confirm('Delete this fare rule?');">
                                <button type="submit" class="btn btn-sm btn-danger">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
