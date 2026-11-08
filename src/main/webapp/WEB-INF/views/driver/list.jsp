<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Drivers" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="page-header">
    <h2 class="mb-0">Drivers</h2>
    <a href="/drivers/new" class="btn btn-success">+ Register Driver</a>
</div>

<form method="get" action="/drivers" class="row g-2 mb-3">
    <div class="col-12 col-md-6">
        <input type="text" name="keyword" class="form-control"
               placeholder="Search by name, email or licence number" value="${fn:escapeXml(keyword)}">
    </div>
    <div class="col-auto">
        <button type="submit" class="btn btn-outline-success">Search</button>
        <a href="/drivers" class="btn btn-outline-secondary">Clear</a>
    </div>
</form>

<c:choose>
    <c:when test="${empty drivers}">
        <div class="alert alert-info">No drivers found.</div>
    </c:when>
    <c:otherwise>
        <div class="table-responsive">
            <table class="table table-striped table-hover bg-white shadow-sm">
                <thead class="table-dark">
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Phone</th>
                    <th>Licence</th>
                    <th>Licence status</th>
                    <th>Experience</th>
                    <th>Availability</th>
                    <th>Active</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="driver" items="${drivers}">
                    <tr>
                        <td>${driver.userId}</td>
                        <td><c:out value="${driver.fullName}"/></td>
                        <td><c:out value="${driver.phone}"/></td>
                        <td><c:out value="${driver.licenceNumber}"/><br>
                            <small class="text-muted">Expires ${driver.licenceExpiry}</small></td>
                        <td>
                            <c:choose>
                                <c:when test="${driver.licenceStatus == 'Valid'}"><span class="badge bg-success">Valid</span></c:when>
                                <c:when test="${driver.licenceStatus == 'Expiring soon'}"><span class="badge bg-warning text-dark">Expiring soon</span></c:when>
                                <c:otherwise><span class="badge bg-danger">Expired</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td>${driver.yearsOfExperience} yr(s)</td>
                        <td><span class="badge ${driver.availability.badgeClass}">${driver.availability.label}</span></td>
                        <td>${driver.active ? 'Yes' : 'No'}</td>
                        <td class="action-buttons">
                            <a href="/drivers/${driver.userId}/trips" class="btn btn-sm btn-info">Trips</a>
                            <a href="/drivers/${driver.userId}/edit" class="btn btn-sm btn-warning">Edit</a>
                            <form method="post" action="/drivers/${driver.userId}/delete" class="d-inline"
                                  onsubmit="return confirm('Delete this driver?');">
                                <button type="submit" class="btn btn-sm btn-danger">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <p class="text-muted">Total: ${fn:length(drivers)} driver(s)</p>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
