<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Customers" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="page-header">
    <h2 class="mb-0">Customers</h2>
    <a href="/customers/new" class="btn btn-primary">+ Register Customer</a>
</div>

<%-- Search box: sends ?keyword=... to GET /customers --%>
<form method="get" action="/customers" class="row g-2 mb-3">
    <div class="col-12 col-md-6">
        <input type="text" name="keyword" class="form-control"
               placeholder="Search by name, email or phone" value="${fn:escapeXml(keyword)}">
    </div>
    <div class="col-auto">
        <button type="submit" class="btn btn-outline-primary">Search</button>
        <a href="/customers" class="btn btn-outline-secondary">Clear</a>
    </div>
</form>

<c:choose>
    <c:when test="${empty customers}">
        <div class="alert alert-info">No customers found.</div>
    </c:when>
    <c:otherwise>
        <div class="table-responsive">
            <table class="table table-striped table-hover bg-white shadow-sm">
                <thead class="table-dark">
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Address</th>
                    <th>Age</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="customer" items="${customers}">
                    <tr>
                        <td>${customer.userId}</td>
                        <td><c:out value="${customer.fullName}"/></td>
                        <td><c:out value="${customer.email}"/></td>
                        <td><c:out value="${customer.phone}"/></td>
                        <td><c:out value="${customer.address}"/></td>
                        <td>${customer.age}</td>
                        <td>
                            <c:choose>
                                <c:when test="${customer.active}"><span class="badge bg-success">Active</span></c:when>
                                <c:otherwise><span class="badge bg-secondary">Inactive</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td class="action-buttons">
                            <a href="/customers/${customer.userId}/edit" class="btn btn-sm btn-warning">Edit</a>
                            <form method="post" action="/customers/${customer.userId}/delete" class="d-inline"
                                  onsubmit="return confirm('Delete this customer?');">
                                <button type="submit" class="btn btn-sm btn-danger">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <p class="text-muted">Total: ${fn:length(customers)} customer(s)</p>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
