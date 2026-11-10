<%-- Shared top part of every page: <head>, navigation bar and success/error messages.
     Each page sets ${pageTitle} first and then includes this file.
     A page can also set ${fullWidth} = true to draw edge-to-edge sections (the home page does). --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="currentUser" value="${sessionScope.currentUser}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${pageTitle}"/> | Vehicle Rental System</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
</head>
<body class="d-flex flex-column min-vh-100">

<nav class="navbar navbar-expand-lg navbar-dark app-navbar sticky-top">
    <div class="container">
        <a class="navbar-brand d-flex align-items-center gap-2" href="${empty currentUser ? '/' : currentUser.homeUrl}">
            <span class="brand-mark"><i class="bi bi-car-front-fill"></i></span>
            <span class="fw-bold">Vehicle Rental</span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav"
                aria-controls="mainNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="mainNav">
            <ul class="navbar-nav ms-auto align-items-lg-center gap-lg-1">
                <c:choose>
                    <%-- Guest (not logged in) --%>
                    <c:when test="${empty currentUser}">
                        <li class="nav-item"><a class="nav-link" href="/">Home</a></li>
                        <li class="nav-item"><a class="nav-link" href="/login">Log in</a></li>
                        <li class="nav-item ms-lg-2"><a class="btn btn-primary btn-sm px-3" href="/signup">Sign up</a></li>
                    </c:when>

                    <%-- Customer --%>
                    <c:when test="${currentUser.customer}">
                        <li class="nav-item"><a class="nav-link" href="/customer/dashboard"><i class="bi bi-grid"></i> Dashboard</a></li>
                        <li class="nav-item"><a class="nav-link" href="/customer/book"><i class="bi bi-plus-circle"></i> Book a ride</a></li>
                        <li class="nav-item"><a class="nav-link" href="/customer/rides"><i class="bi bi-clock-history"></i> My rides</a></li>
                        <li class="nav-item"><a class="nav-link" href="/customer/feedback"><i class="bi bi-chat-heart"></i> Feedback</a></li>
                    </c:when>

                    <%-- Driver --%>
                    <c:when test="${currentUser.driver}">
                        <li class="nav-item"><a class="nav-link" href="/driver/dashboard"><i class="bi bi-speedometer2"></i> Ride requests</a></li>
                    </c:when>

                    <%-- Admin: links to every member's management module --%>
                    <c:otherwise>
                        <li class="nav-item"><a class="nav-link" href="/admin">Admin</a></li>
                        <li class="nav-item"><a class="nav-link" href="/customers">Customers</a></li>
                        <li class="nav-item"><a class="nav-link" href="/drivers">Drivers</a></li>
                        <li class="nav-item"><a class="nav-link" href="/vehicles">Vehicles</a></li>
                        <li class="nav-item"><a class="nav-link" href="/bookings">Bookings</a></li>
                        <li class="nav-item"><a class="nav-link" href="/feedback">Feedback</a></li>
                    </c:otherwise>
                </c:choose>

                <%-- Logged-in user menu --%>
                <c:if test="${not empty currentUser}">
                    <li class="nav-item dropdown ms-lg-2">
                        <a class="nav-link dropdown-toggle user-chip" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                            <span class="avatar">${fn:substring(currentUser.firstName, 0, 1)}</span>
                            <c:out value="${currentUser.firstName}"/>
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end shadow">
                            <li><span class="dropdown-item-text small text-muted"><c:out value="${currentUser.email}"/><br>${currentUser.role}</span></li>
                            <li><hr class="dropdown-divider"></li>
                            <li>
                                <form method="post" action="/logout" class="m-0">
                                    <button type="submit" class="dropdown-item text-danger"><i class="bi bi-box-arrow-right"></i> Log out</button>
                                </form>
                            </li>
                        </ul>
                    </li>
                </c:if>
            </ul>
        </div>
    </div>
</nav>

<main class="flex-grow-1 ${fullWidth ? '' : 'container py-4 py-md-5'}">

    <%-- Messages sent from controllers with addFlashAttribute(...) or model.addAttribute(...) --%>
    <div class="${fullWidth ? 'container pt-3' : ''}">
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle-fill"></i> <c:out value="${successMessage}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle-fill"></i> <c:out value="${errorMessage}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
    </div>
