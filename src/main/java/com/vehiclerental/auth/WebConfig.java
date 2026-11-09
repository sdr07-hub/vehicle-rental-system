package com.vehiclerental.auth;

import com.vehiclerental.model.UserRole;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Tells Spring which addresses need which role.
 * "/customer/**" means /customer and everything under it (it does NOT match /customers).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RoleInterceptor(UserRole.CUSTOMER))
                .addPathPatterns("/customer/**");

        registry.addInterceptor(new RoleInterceptor(UserRole.DRIVER))
                .addPathPatterns("/driver/**");

        registry.addInterceptor(new RoleInterceptor(UserRole.ADMIN))
                .addPathPatterns("/admin/**", "/bookings/**"); // management pages are for admins only
        // Optional: also lock "/customers/**" and "/vehicles/**" the same way (not "/drivers/**": drivers use its trips page)
    }
}
