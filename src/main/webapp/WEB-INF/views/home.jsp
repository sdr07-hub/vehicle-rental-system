<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <!-- Makes the page fit mobile screens -->
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Vehicle Rental System</title>
    <!-- Bootstrap: ready-made styles for buttons, cards and layout -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light d-flex flex-column min-vh-100">

<!-- Top navigation bar -->
<nav class="navbar navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand fw-bold" href="/">Vehicle Rental System</a>
    </div>
</nav>

<!-- Main content -->
<main class="container py-5 flex-grow-1">
    <h1 class="text-center mb-2">Welcome</h1>
    <p class="text-center text-muted mb-5">Choose how you want to continue</p>

    <div class="row g-4">
        <!-- Customer card -->
        <div class="col-12 col-md-4">
            <div class="card h-100 text-center shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">Customer</h5>
                    <p class="card-text">Book a vehicle and view your trips.</p>
                    <a href="/customers" class="btn btn-primary">Go to Customer</a>
                </div>
            </div>
        </div>

        <!-- Driver card -->
        <div class="col-12 col-md-4">
            <div class="card h-100 text-center shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">Driver</h5>
                    <p class="card-text">Manage your availability and assigned trips.</p>
                    <a href="/drivers" class="btn btn-success">Go to Driver</a>
                </div>
            </div>
        </div>

        <!-- Admin card -->
        <div class="col-12 col-md-4">
            <div class="card h-100 text-center shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">Admin</h5>
                    <p class="card-text">Manage vehicles, bookings, fares and feedback.</p>
                    <a href="/admin" class="btn btn-dark">Go to Admin</a>
                </div>
            </div>
        </div>
    </div>
</main>

<!-- Footer -->
<footer class="bg-dark text-white text-center py-3">
    &copy; 2026 Vehicle Rental System
</footer>

</body>
</html>