package com.vehiclerental.auth;

import com.vehiclerental.model.User;
import com.vehiclerental.model.UserRole;
import jakarta.servlet.http.HttpSession;

import java.io.Serializable;

/**
 * The small "who is logged in" object kept in the HTTP session.
 * Only safe details are stored here - never the password.
 *
 * OOP: Encapsulation (private final fields, getters only) and Information hiding.
 */
public class SessionUser implements Serializable {

    /** The name used to store this object in the session. JSP pages read ${sessionScope.currentUser}. */
    public static final String SESSION_KEY = "currentUser";

    private final Long userId;
    private final String fullName;
    private final String email;
    private final UserRole role;

    public SessionUser(User user) {
        this.userId = user.getUserId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

    /** Reads the logged-in user from the session. Returns null if nobody is logged in. */
    public static SessionUser from(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(SESSION_KEY);
        return (value instanceof SessionUser user) ? user : null;
    }

    /** The page each role sees straight after logging in. */
    public String getHomeUrl() {
        return switch (role) {
            case CUSTOMER -> "/customer/dashboard";
            case DRIVER -> "/driver/dashboard";
            case ADMIN -> "/admin";
        };
    }

    /** "Nimal Perera" -> "Nimal" (used for the greeting). */
    public String getFirstName() {
        if (fullName == null || fullName.isBlank()) {
            return "there";
        }
        return fullName.trim().split("\\s+")[0];
    }

    public boolean isCustomer() {
        return role == UserRole.CUSTOMER;
    }

    public boolean isDriver() {
        return role == UserRole.DRIVER;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }
}
