package com.vehiclerental.config;

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
                .addPathPatterns("/admin/**");
        // Optional: also lock the members' management pages to admins by adding them here, e.g.
        // .addPathPatterns("/admin/**", "/customers/**", "/vehicles/**", "/bookings/**");
    }
}