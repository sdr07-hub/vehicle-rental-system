package com.vehiclerental.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Feedback access rules using the session established by LoginController. */
@Configuration
public class FeedbackWebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requireRole("CUSTOMER"))
                .addPathPatterns("/customer/feedback", "/customer/feedback/**");
        registry.addInterceptor(requireRole("ADMIN"))
                .addPathPatterns("/feedback", "/feedback/**");
    }

    private HandlerInterceptor requireRole(String requiredRole) {
        return new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request,
                                     HttpServletResponse response, Object handler) throws Exception {
                HttpSession session = request.getSession(false);
                if (session == null || !(session.getAttribute("loggedInUserId") instanceof Long)) {
                    response.sendRedirect(request.getContextPath() + "/login");
                    return false;
                }
                Object role = session.getAttribute("loggedInRole");
                if (!requiredRole.equals(role)) {
                    String home = "ADMIN".equals(role) ? "/admin"
                            : "DRIVER".equals(role) ? "/drivers"
                            : "CUSTOMER".equals(role) ? "/customers" : "/login";
                    response.sendRedirect(request.getContextPath() + home);
                    return false;
                }
                return true;
            }
        };
    }
}
