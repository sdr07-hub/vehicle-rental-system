
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="${editMode ? 'Review Feedback' : 'Submit Feedback'}" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<c:set var="formAction" value="${editMode ? ('/feedback/' += feedbackId) : '/feedback'}"/>

<div class="row justify-content-center">
  <div class="col-12 col-md-10 col-lg-7">
    <div class="card shadow-sm">
       <div class="card-body">
         <h3 class="card-title mb-4">${editMode ? ('Review Feedback #' += feedbackId) : 'Submit Feedback'}</h3>

        <form method="post" action="${formAction}" novalidate>

           <div class="mb-3">
             <label class="form-label" for="customerId">Customer *</label>
             <select id="customerId" name="customerId"
                  class="form-select ${not empty errors.customerId ? 'is-invalid' : ''}">
               <option value="">-- Choose a customer --</option>
               <c:forEach var="o" items="${customerOptions}">
                  <option value="${o.id}" ${feedback.customerId == o.id ? 'selected' : ''}><c:out value="${o.label}"/></option>
               </c:forEach>
             </select>
             <div class="invalid-feedback">${errors.customerId}</div>
          </div>

          <div class="mb-3">
             <label class="form-label" for="bookingId">Booking (optional)</label>
             <select id="bookingId" name="bookingId"
                  class="form-select ${not empty errors.bookingId ? 'is-invalid' : ''}">
               <option value="">-- Not about a booking --</option>
               <c:forEach var="o" items="${bookingOptions}">
                  <option value="${o.id}" ${feedback.bookingId == o.id ? 'selected' : ''}><c:out value="${o.label}"/></option>
               </c:forEach>
             </select>
             <div class="invalid-feedback">${errors.bookingId}</div>
          </div>

           <div class="row">
             <div class="col-12 col-md-6 mb-3">
                <label class="form-label" for="feedbackType">Type *</label>
                <select id="feedbackType" name="feedbackType"
                     class="form-select ${not empty errors.feedbackType ? 'is-invalid' : ''}">
                  <c:forEach var="t" items="${types}">
                     <option value="${t}" ${feedback.feedbackType == t ? 'selected' : ''}>${t.label}</option>
                  </c:forEach>
                </select>
                <div class="invalid-feedback">${errors.feedbackType}</div>
             </div>
             <div class="col-12 col-md-6 mb-3">
                <label class="form-label" for="rating">Rating *</label>
                <select id="rating" name="rating"
                     class="form-select ${not empty errors.rating ? 'is-invalid' : ''}">
                  <c:forEach var="r" begin="1" end="5">
                     <option value="${6 - r}" ${feedback.rating == (6 - r) ? 'selected' : ''}>${6 - r} star(s)</option>
                  </c:forEach>
                </select>
                <div class="invalid-feedback">${errors.rating}</div>
             </div>
           </div>

           <div class="mb-3">
             <label class="form-label" for="subject">Subject *</label>
             <input type="text" id="subject" name="subject" maxlength="150"
                 class="form-control ${not empty errors.subject ? 'is-invalid' : ''}"
                 value="${fn:escapeXml(feedback.subject)}">
             <div class="invalid-feedback">${errors.subject}</div>
           </div>

          <div class="mb-3">
            <label class="form-label" for="message">Message *</label>
            <textarea id="message" name="message" rows="4" maxlength="1000"
                  class="form-control ${not empty errors.message ? 'is-invalid' : ''}">${fn:escapeXml(feedback.message)}</textarea>
            <div class="invalid-feedback">${errors.message}</div>
          </div>

           <c:if test="${editMode}">
             <hr>
             <h6 class="text-muted">Admin review</h6>

                  <div class="mb-3">
                    <label class="form-label" for="status">Status *</label>
                    <select id="status" name="status"
                         class="form-select ${not empty errors.status ? 'is-invalid' : ''}">
                       <c:forEach var="s" items="${statuses}">
                         <option value="${s}" ${feedback.status == s ? 'selected' : ''}>${s.label}</option>
                       </c:forEach>
                    </select>
                    <div class="invalid-feedback">${errors.status}</div>
                  </div>

            <div class="mb-3">
               <label class="form-label" for="adminResponse">Reply to customer</label>
               <textarea id="adminResponse" name="adminResponse" rows="3" maxlength="1000"
                     class="form-control ${not empty errors.adminResponse ? 'is-invalid' : ''}">${fn:escapeXml(feedback.adminResponse)}</textarea>
               <div class="invalid-feedback">${errors.adminResponse}</div>
               <div class="form-hint">A suggested reply is filled in for you. Edit it before saving.</div>
            </div>
          </c:if>

             <div class="d-flex gap-2">
               <button type="submit" class="btn btn-primary">${editMode ? 'Save Review' : 'Submit'}</button>
               <a href="/feedback" class="btn btn-outline-secondary">Cancel</a>
             </div>
          </form>
       </div>
     </div>
  </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
