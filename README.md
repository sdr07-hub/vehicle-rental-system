# Vehicle Rental System

A six-member university Object-Oriented Programming project for a mobile-friendly vehicle rental and ride-booking platform. Customers can request a vehicle, drivers can manage their availability and assigned trips, and administrators can manage the system through an admin dashboard.

## Project goals

The application will use Java, Spring Boot, JSP, HTML, CSS, JavaScript, and MySQL. Every member owns a separate module and implements Create, Read, Update, and Delete (CRUD) operations for that module.

| User type | Main tasks |
|---|---|
| Customer | Register, update profile, browse vehicles, create and manage bookings, submit feedback |
| Driver | Register, update profile and licence details, set availability, view assigned bookings |
| Administrator | Manage customers, drivers, vehicles, bookings, fare rules, feedback, and admin accounts |

## Team workload and branches

Each member develops and commits their work on the assigned branch. Work is merged into `main` only after it has been checked by the team.

| Member | Module | Branch | CRUD responsibilities |
|---|---|---|---|
| 1 | Customer Management | `feature/customers` | Create customer accounts; read/search customer details; update customer profiles; delete or deactivate customers |
| 2 | Driver Management | `feature/drivers` | Create driver accounts; read/search driver details; update licence, phone number, and availability; delete or deactivate drivers |
| 3 | Vehicle Management | `feature/vehicles` | Add vehicles; list/search vehicles; update vehicle details and status; remove vehicles |
| 4 | Booking and Trip Management | `feature/bookings` | Create bookings; view booking history; update assigned driver and trip status; cancel bookings |
| 5 | Admin and Fare Management | `feature/admin-fares` | Create admin accounts and fare rules; view admin accounts and fares; update permissions and fare rules; remove admin accounts or fare rules |
| 6 | Feedback and Complaint Management | `feature/feedback` | Submit feedback or complaints; view feedback; update feedback or complaint status; delete feedback |

## Main features

| Feature | Description |
|---|---|
| Authentication | Separate login access for customers, drivers, and administrators |
| Mobile-friendly driver access | Drivers can use a phone browser to update availability and view assigned trips |
| Vehicle catalogue | Customers can view available vehicles and vehicle information |
| Booking | Customers can request a vehicle, see booking status, and cancel a booking when permitted |
| Driver assignment | An administrator assigns available drivers to bookings |
| Fare rules | Administrators manage the base fare and price-per-kilometre rules |
| Feedback | Customers submit feedback or complaints; administrators review and update their status |

## Technology stack

| Area | Technology |
|---|---|
| Programming language | Java 21 |
| Backend | Spring Boot 3.5.x |
| Build tool | Maven |
| User interface | JSP, HTML, CSS, JavaScript, Bootstrap |
| Database | MySQL 8.0 |
| Database tool | MySQL Workbench |
| Version control | Git and GitHub |
| Development environment | IntelliJ IDEA Community Edition |

## Object-oriented programming plan

| OOP concept | Planned use in the project |
|---|---|
| Encapsulation | Private fields with public getters and setters in classes such as `Customer`, `Driver`, `Vehicle`, and `Booking` |
| Inheritance | `Customer`, `Driver`, and `Admin` inherit common data from a `User` class; vehicle types can inherit from a `Vehicle` class |
| Polymorphism | Vehicle fare calculations and user dashboard behaviour can use overridden methods for different types |
| Abstraction | Interfaces or abstract classes define common operations such as calculating a fare or managing a user |
| Information hiding | Database passwords and internal implementation details stay outside JSP pages and are accessed only through services |

## Planned database tables

| Table | Purpose |
|---|---|
| `users` | Common login details and role for all users |
| `customers` | Customer contact and profile details |
| `drivers` | Driver licence, contact, availability, and profile details |
| `vehicles` | Vehicle registration number, type, capacity, status, and pricing information |
| `bookings` | Pickup location, destination, booking date, status, customer, vehicle, and driver references |
| `fare_rules` | Base fare, price per kilometre, and vehicle type rules |
| `feedback` | Customer ratings, comments, complaint status, and booking reference |

## Project structure

```text
src/
  main/
    java/        Java classes: models, controllers, services, repositories
    resources/   Application settings and database connection settings
    webapp/      JSP pages, CSS, JavaScript, and images
```

## GitHub workflow

1. Switch to your assigned feature branch before making changes.
2. Pull the latest changes from `main` when the team asks you to update.
3. Complete a small piece of your assigned work.
4. Commit with a clear message, such as `Add customer registration form`.
5. Push your branch to GitHub.
6. Create a pull request from your feature branch into `main` when your CRUD work is tested.
7. Do not edit another member's module without discussing it with them first.

## Running the project

This section will be updated after the Spring Boot project and MySQL database are created.

## Current status

| Item | Status |
|---|---|
| GitHub repository | Created |
| Team branches | Created |
| IntelliJ project | Created - Spring Boot, Maven, JSP home page |
| MySQL database | Created - 7 tables (see database/schema.sql) |
| CRUD modules | Planned |

## Setup for team members (one-time)

1. Clone the repository in IntelliJ: File > New > Project from Version Control > paste the GitHub URL.
2. Switch to your own branch from the branch menu at the top left (for example feature/customers).
3. Maven setting: File > Settings > Build, Execution, Deployment > Build Tools > Maven. Set "Maven home path" to "Bundled (Maven 3)", click OK, then reload Maven.
4. Create the database: open database/schema.sql in MySQL Workbench (connected as root) and click the lightning bolt. Run this only once, on a fresh setup, because it recreates the tables empty.
5. Create the app database user in MySQL Workbench (choose your own password):
   CREATE USER IF NOT EXISTS 'vehicle_app'@'localhost' IDENTIFIED BY 'YOUR_PASSWORD';
   ALTER USER 'vehicle_app'@'localhost' IDENTIFIED BY 'YOUR_PASSWORD';
   GRANT ALL PRIVILEGES ON vehicle_rental_db.* TO 'vehicle_app'@'localhost';
   FLUSH PRIVILEGES;
6. Create your private password file: in src/main/resources, create db-local.properties with this one line:
   spring.datasource.password=YOUR_PASSWORD
   This file is ignored by Git. Never commit it.
7. Run VehicleRentalSystemApplication with the green Run button and open http://localhost:8081

If you see "Port 8081 was already in use", stop the old run (red square) and run again.