# Project map - who built what

Use this page in the report and the viva to find each member's code quickly.
All Java code is inside `src/main/java/com/vehiclerental`. All pages are inside `src/main/webapp/WEB-INF/views`.

| Part | Owner | Java files | JSP folder | Main address |
| --- | --- | --- | --- | --- |
| Shared base | Leader | `model/BaseEntity`, `model/User`, `model/UserRole`, `repository/UserRepository`, `config/PasswordConfig`, `util/FormErrors` | `common/` (header, footer), `home.jsp` | / |
| Login, sign-up, page protection | Leader | everything in the `auth/` package | `auth/` | /login, /signup |
| Customer & driver ride portal | Leader | everything in the `portal/` package | `portal/` | /customer/dashboard, /driver/dashboard |
| Customer management | Member 1 | `Customer*` in model, repository, service, controller | `customer/` | /customers |
| Driver management | Member 2 | `Driver*` in model, repository, service, controller | `driver/` | /drivers |
| Vehicle management | Member 3 | `Vehicle*`, `Car`, `Van`, `Suv` in model, repository, service, controller | `vehicle/` | /vehicles |
| Booking management | Member 4 | `Booking*`, `PricingStrategy`, `StandardPricing`, `PeakHourPricing` | `booking/` | /bookings |
| Admin and fares | Member 5 | `Admin*`, `FareRule*`, `FareCalculator`, `NightFareCalculator` | `admin/` | /admin |
| Feedback | Member 6 | `Feedback*` in model, repository, service, controller | `feedback/` | /feedback |

## Leader packages

```
com.vehiclerental
├── auth                      Login, sign-up, log-out, who is logged in, page protection
│   ├── AuthController
│   ├── AuthService / AuthServiceImpl
│   ├── SessionUser
│   ├── RoleInterceptor
│   └── WebConfig
└── portal                    Customer books a ride, driver accepts / starts / finishes it
    ├── LanAddressPrinter     Prints the address to open on a phone
    ├── controller            CustomerPortalController, DriverPortalController
    ├── service               CustomerRideService(+Impl), DriverRideService(+Impl)
    ├── repository            RideRepository (SQL with JdbcTemplate)
    ├── pricing               RideFareCalculator, RideFareRule
    └── model                 RideView, RideStatus, VehicleTypeView, RideRequestForm
```

The portal reads and writes the shared tables (`bookings`, `vehicles`, `fare_rules`, `users`) with SQL,
so it works on its own and does not change any member's classes. Rides booked in the portal also appear in
Member 4's /bookings admin page, because both use the same `bookings` table and the same status values.

## Database tables

| Table | Written by |
| --- | --- |
| users, customers | Member 1, sign-up (Leader) |
| users, drivers | Member 2 |
| vehicles | Member 3 |
| bookings | Member 4, portal (Leader) |
| users, admins, fare_rules | Member 5 |
| feedback | Member 6 |
