<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Give feedback" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="page-header">
    <div>
        <h1 class="h3 fw-bold mb-0">Give feedback</h1>
        <p class="text-muted mb-0">Tell us how it went. Complaints go straight to the admin team.</p>
    </div>
    <a href="/customer/feedback" class="btn btn-outline-secondary"><i class="bi bi-arrow-left"></i> My feedback</a>
</div>

<div class="row justify-content-center">
    <div class="col-12 col-lg-8">
        <div class="card border-0 shadow-sm">
            <div class="card-body p-4">
                <form method="post" action="/customer/feedback" novalidate>

                    <%-- Which ride (filled in when the customer came from "Rate ride") --%>
                    <c:if test="${not empty feedbackForm.bookingId}">
                        <input type="hidden" name="bookingId" value="${feedbackForm.bookingId}">
                        <div class="alert alert-light border mb-4">
                            <i class="bi bi-car-front"></i> About ride <strong>#${feedbackForm.bookingId}</strong>
                        </div>
                    </c:if>
                    <c:if test="${not empty errors.bookingId}">
                        <div class="alert alert-danger">${errors.bookingId}</div>
                    </c:if>

                    <%-- 1. Type --%>
                    <label class="form-label fw-semibold">1. What kind of message?</label>
                    <div class="d-flex flex-wrap gap-2 mb-4">
                        <c:forEach var="t" items="${types}">
                            <input type="radio" class="btn-check" name="feedbackType" id="type-${t}" value="${t}"
                                   ${feedbackForm.feedbackType == t ? 'checked' : ''}>
                            <label class="btn btn-outline-primary" for="type-${t}">
                                <i class="bi ${t == 'COMPLAINT' ? 'bi-exclamation-triangle' : 'bi-chat-heart'}"></i> ${t.label}
                            </label>
                        </c:forEach>
                    </div>

                    <%-- 2. Rating: five buttons, 5 = best --%>
                    <label class="form-label fw-semibold">2. Your rating</label>
                    <div class="d-flex flex-wrap gap-2 mb-1">
                        <c:forEach var="r" begin="1" end="5">
                            <input type="radio" class="btn-check" name="rating" id="rating-${r}" value="${r}"
                                   ${feedbackForm.rating == r ? 'checked' : ''}>
                            <label class="btn btn-outline-warning" for="rating-${r}">${r} <i class="bi bi-star-fill"></i></label>
                        </c:forEach>
                    </div>
                    <div class="form-hint mb-4">1 = very poor, 5 = excellent.</div>
                    <c:if test="${not empty errors.rating}"><div class="text-danger small mb-3">${errors.rating}</div></c:if>

                    <%-- 3. Subject and message --%>
                    <div class="mb-3">
                        <label class="form-label fw-semibold" for="subject">3. Subject *</label>
                        <input type="text" id="subject" name="subject" maxlength="150"
                               class="form-control ${not empty errors.subject ? 'is-invalid' : ''}"
                               placeholder="e.g. Friendly driver, on time"
                               value="${fn:escapeXml(feedbackForm.subject)}">
                        <div class="invalid-feedback">${errors.subject}</div>
                    </div>

                    <div class="mb-4">
                        <label class="form-label fw-semibold" for="message">Message *</label>
                        <textarea id="message" name="message" rows="5" maxlength="1000"
                                  class="form-control ${not empty errors.message ? 'is-invalid' : ''}"
                                  placeholder="What went well, or what went wrong?">${fn:escapeXml(feedbackForm.message)}</textarea>
                        <div class="invalid-feedback">${errors.message}</div>
                    </div>

                    <button type="submit" class="btn btn-primary btn-lg w-100"><i class="bi bi-send"></i> Send feedback</button>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
