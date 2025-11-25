package com.teamup.main.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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
                        "/public/**", // file tĩnh hay API công khai
                        "/error", // tránh vòng lặp lỗi
                        "/favicon.ico" // khỏi intercept mấy request linh tinh
                );
    }

}
