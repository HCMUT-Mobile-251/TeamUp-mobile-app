package com.teamup.main.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.teamup.main.middleware.RoleInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private RoleInterceptor roleInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(roleInterceptor)
                .addPathPatterns("/**") // chặn tất cả
                .excludePathPatterns(
                        "/auth/**", // mấy API login, callback Google
                        "/auth-redirect.html", // OAuth redirect page
                        "/**/auth-redirect.html", // OAuth redirect page (any path)
                        "/*.html", // All HTML files in root
                        "/public/**", // file tĩnh hay API công khai
                        "/error", // tránh vòng lặp lỗi
                        "/favicon.ico", // khỏi intercept mấy request linh tinh
                        "/course/**", // dev: tạm thời cho phép truy cập course API
                        "/group/**", // dev: tạm thời cho phép truy cập group API
                        "/tag/**", // dev: tạm thời cho phép truy cập tag API
                        "/search/**", // dev: tạm thời cho phép truy cập search API
                        "/notification/**", // dev: tạm thời cho phép truy cập notification API
                        "/user/**" // dev: tạm thời cho phép truy cập user API
                );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8081", "http://localhost:19006", "exp://192.168.*:8081")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

}
