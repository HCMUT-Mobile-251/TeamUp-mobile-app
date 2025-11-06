package com.teamup.main.middleware;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamup.main.dto.response.ApiResponse;
import com.teamup.main.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * nếu vô role-user thì Check token_access
 * nếu vô role-admin thì check thêm gmail 
 * Return "false" chặn request và ngược lại
 */
@Component
public class RoleInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // lấy token từ header
        String googleIdTokenString = request.getHeader("Authorization");
        if (googleIdTokenString != null && googleIdTokenString.startsWith("Bearer ")) {
            // bỏ "Bearer "
            googleIdTokenString = googleIdTokenString.substring(7);
        } else {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpServletResponse.SC_UNAUTHORIZED)
                    .message("Thiếu hoặc cấu trúc không hợp lệ!")
                    .result(null)
                    .build();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            new ObjectMapper().writeValue(response.getWriter(), apiResponse);
            return false;
        }

        // xử lý verify token
        try {
            AuthService authService = new AuthService();
            ApiResponse<Boolean> apiResponse = authService.verifyAccessToken(googleIdTokenString);
            if (!apiResponse.getResult()) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                new ObjectMapper().writeValue(response.getWriter(), apiResponse);
                return false;
            }

            // check route-admin
            String path = request.getRequestURI();
            if (path.startsWith("/admin") && !apiResponse.getMessage().equals("Admin")) {
                ApiResponse<Object> apiResponse1 = ApiResponse.builder()
                        .code(HttpServletResponse.SC_FORBIDDEN)
                        .message("Bạn không có quyền truy cập tài nguyên này!")
                        .result(null)
                        .build();
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);

                new ObjectMapper().writeValue(response.getWriter(), apiResponse1);
                return false;
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            new ObjectMapper().writeValue(response.getWriter(), apiResponse);
            return true;
        } catch (IOException e) {
            // Nếu có lỗi trong quá trình verify (ví dụ token lỗi, mạng lỗi,…)
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpServletResponse.SC_BAD_GATEWAY)
                    .message("Xác thực thỉnh thoảng lỗi: " + e.getMessage())
                    .result(null)
                    .build();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);

            new ObjectMapper().writeValue(response.getWriter(), apiResponse);
            return false;
        }
    }
}