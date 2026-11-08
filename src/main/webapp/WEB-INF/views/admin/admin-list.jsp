<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Admins" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="page-header">
    <h2 class="mb-0">Admin Accounts</h2>
    <div class="d-flex gap-2">
        <a href="/admin" class="btn btn-outline-secondary">Dashboard</a>
        <a href="/admin/admins/new" class="btn btn-dark">+ Add Admin</a>
    </div>
</div>

<div class="table-responsive">
    <table class="table table-striped table-hover bg-white shadow-sm">
        <thead class="table-dark">
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Phone</th>
            <th>Level</th>
            <th>Active</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="admin" items="${admins}">
            <tr>
                <td>${admin.userId}</td>
                <td><c:out value="${admin.fullName}"/></td>
                <td><c:out value="${admin.email}"/></td>
                <td><c:out value="${admin.phone}"/></td>
                <td>${admin.adminLevel.label}</td>
                <td>${admin.active ? 'Yes' : 'No'}</td>
                <td class="action-buttons">
                    <a href="/admin/admins/${admin.userId}/edit" class="btn btn-sm btn-warning">Edit</a>
                    <form method="post" action="/admin/admins/${admin.userId}/delete" class="d-inline"
                          onsubmit="return confirm('Delete this admin?');">
                        <button type="submit" class="btn btn-sm btn-danger">Delete</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<p class="text-muted">Total: ${fn:length(admins)} admin(s)</p>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
