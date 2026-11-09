
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Feedback" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="page-header">
  <h2 class="mb-0">Feedback and Complaints</h2>
  <a href="/feedback/new" class="btn btn-primary">+ Submit Feedback</a>
</div>

<%-- Filters --%>
<form method="get" action="/feedback" class="row g-2 mb-3">
   <div class="col-6 col-md-3">
     <select name="type" class="form-select">
        <option value="">All types</option>
        <c:forEach var="t" items="${types}">
          <option value="${t}" ${selectedType == t ? 'selected' : ''}>${t.label}</option>
        </c:forEach>
     </select>
   </div>
   <div class="col-6 col-md-3">
     <select name="status" class="form-select">
        <option value="">All statuses</option>
        <c:forEach var="s" items="${statuses}">
          <option value="${s}" ${selectedStatus == s ? 'selected' : ''}>${s.label}</option>
        </c:forEach>
     </select>
   </div>
   <div class="col-auto">
     <button type="submit" class="btn btn-outline-primary">Filter</button>
     <a href="/feedback" class="btn btn-outline-secondary">Clear</a>
   </div>
</form>

<c:choose>
  <c:when test="${empty feedbackList}">
     <div class="alert alert-info">No feedback found.</div>
  </c:when>
  <c:otherwise>
     <div class="table-responsive">
       <table class="table table-striped table-hover bg-white shadow-sm">
         <thead class="table-dark">
         <tr>
            <th>#</th>
            <th>Customer</th>
            <th>Type</th>
            <th>Rating</th>
            <th>Subject</th>
            <th>Priority</th>
            <th>Status</th>
            <th>Actions</th>
         </tr>
         </thead>
         <tbody>
         <c:forEach var="f" items="${feedbackList}">
            <tr>
              <td>${f.feedbackId}</td>
              <td>
                 <c:out value="${customerNames[f.customerId]}"/>
                 <c:if test="${not empty f.bookingId}"><br><small class="text-muted">Booking
#${f.bookingId}</small></c:if>
              </td>
              <td>${f.feedbackType.label}</td>
              <td class="text-warning">${f.stars}</td>
              <td>
                 <strong><c:out value="${f.subject}"/></strong><br>
                 <small class="text-muted"><c:out value="${f.message}"/></small>
                 <c:if test="${not empty f.adminResponse}">
                   <br><small class="text-success">Reply: <c:out value="${f.adminResponse}"/></small>
                 </c:if>
              </td>
                <td>
                   <%-- getPriority() is worked out by ComplaintResponder or GeneralFeedbackResponder --%>
                   <c:choose>
                      <c:when test="${f.priority == 'HIGH'}"><span class="badge bg-danger">HIGH</span></c:when>
                      <c:when test="${f.priority == 'MEDIUM'}"><span class="badge bg-warning text-dark">MEDIUM</span></c:when>
                      <c:otherwise><span class="badge bg-info text-dark">LOW</span></c:otherwise>
                   </c:choose>
                </td>
                <td><span class="badge ${f.status.badgeClass}">${f.status.label}</span></td>
                <td class="action-buttons">
                   <a href="/feedback/${f.feedbackId}/edit" class="btn btn-sm btn-warning">Review</a>
                   <form method="post" action="/feedback/${f.feedbackId}/delete" class="d-inline"
                       onsubmit="return confirm('Delete this feedback?');">
                      <button type="submit" class="btn btn-sm btn-danger">Delete</button>
                   </form>
                </td>
             </tr>
          </c:forEach>
          </tbody>
       </table>
     </div>
     <p class="text-muted">Total: ${fn:length(feedbackList)} item(s)</p>
  </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>

