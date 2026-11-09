<%-- Shared top part of every page: <head>, navigation bar and success/error messages.
     Each page sets ${pageTitle} first and then includes this file. --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${pageTitle}"/> | Vehicle Rental System</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&family=Poppins:wght@600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
</head>
<body class="bg-light d-flex flex-column min-vh-100">

<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand fw-bold" href="/">Vehicle Rental System</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav"
                aria-controls="mainNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="mainNav">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item"><a class="nav-link" href="/">Home</a></li>
                <li class="nav-item"><a class="nav-link" href="/customers">Customers</a></li>
                <li class="nav-item"><a class="nav-link" href="/drivers">Drivers</a></li>
                <li class="nav-item"><a class="nav-link" href="/vehicles">Vehicles</a></li>
                <li class="nav-item"><a class="nav-link" href="/bookings">Bookings</a></li>
                <li class="nav-item"><a class="nav-link" href="/feedback">Feedback</a></li>
                <li class="nav-item"><a class="nav-link" href="/admin">Admin</a></li>
                <c:if test="${not empty sessionScope.loggedInName}">
                    <li class="nav-item"><span class="nav-link text-warning">${sessionScope.loggedInName}</span></li>
                    <li class="nav-item"><a class="nav-link" href="/logout">Logout</a></li>
                </c:if>
            </ul>
        </div>
    </div>
</nav>

<main class="container py-4 flex-grow-1">

    <%-- Messages sent from controllers with redirectAttributes.addFlashAttribute(...) --%>
    <c:if test="${not empty successMessage}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <c:out value="${successMessage}"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <c:out value="${errorMessage}"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    </c:if>