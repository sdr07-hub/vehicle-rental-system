<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="My feedback" scope="request"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="page-header">
    <div>
        <h1 class="h3 fw-bold mb-0">My feedback</h1>
        <p class="text-muted mb-0">Everything you have sent, and the admin's replies.</p>
    </div>
    <a href="/customer/feedback/new" class="btn btn-primary"><i class="bi bi-plus-circle"></i> Give feedback</a>
</div>

<c:choose>
    <c:when test="${empty feedbackList}">
        <div class="empty-state">
            <i class="bi bi-chat-heart"></i>
            <p>You have not sent any feedback yet. After a ride, use "Rate ride" on My rides.</p>
            <a href="/customer/feedback/new" class="btn btn-primary">Give feedback</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="row g-3">
            <c:forEach var="f" items="${feedbackList}">
                <div class="col-12 col-lg-6">
                    <div class="ride-card">
                        <div class="d-flex justify-content-between align-items-start mb-2">
                            <div>
                                <span class="fw-semibold"><c:out value="${f.subject}"/></span><br>
                                <small class="text-muted">
                                    ${f.feedbackType.label}
                                    <c:if test="${not empty f.bookingId}"> &middot; Ride #${f.bookingId}</c:if>
                                </small>
                            </div>
                            <span class="badge ${f.status.badgeClass}">${f.status.label}</span>
                        </div>

                        <div class="text-warning mb-2" aria-label="${f.rating} out of 5">${f.stars}</div>
                        <p class="mb-3"><c:out value="${f.message}"/></p>

                        <c:choose>
                            <c:when test="${not empty f.adminResponse}">
                                <div class="demo-box">
                                    <div class="fw-semibold mb-1"><i class="bi bi-reply"></i> Reply from the team</div>
                                    <c:out value="${f.adminResponse}"/>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <small class="text-muted"><i class="bi bi-hourglass-split"></i> Waiting for a reply</small>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
