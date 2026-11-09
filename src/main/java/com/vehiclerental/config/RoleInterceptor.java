package com.vehiclerental.config;

import com.vehiclerental.model.UserRole;
import com.vehiclerental.util.SessionUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Runs BEFORE a controller method. It lets the request through only when
 * the logged-in user has the required role.
 *
 *  - nobody logged in      -> sent to /login
 *  - logged in, wrong role -> sent to their own dashboard
 *
 * OOP: one class, three objects (CUSTOMER, DRIVER, ADMIN) - each object stores its own required role.
 */
public class RoleInterceptor implements HandlerInterceptor {

    private final UserRole requiredRole;

    public RoleInterceptor(UserRole requiredRole) {
        this.requiredRole = requiredRole;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // getSession(false) = do not create a new session, just read the existing one (may be null)
        SessionUser user = SessionUser.from(request.getSession(false));

        if (user == null) {
            response.sendRedirect("/login?required");
            return false; // stop: the controller is not called
        }
        if (user.getRole() != requiredRole) {
            response.sendRedirect(user.getHomeUrl());
            return false;
        }
        return true; // allowed: continue to the controller
    }
}