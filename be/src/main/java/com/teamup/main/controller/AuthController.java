package com.teamup.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamup.main.dto.request.AuthRequest;
import com.teamup.main.dto.request.GoogleAccount;
import com.teamup.main.dto.response.ApiResponse;
import com.teamup.main.dto.response.AuthResponse;
import com.teamup.main.service.AuthService;
import com.teamup.main.service.UserService;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AuthController {
    final UserService userService;
    @Autowired
    AuthService authService;

    @GetMapping("/login")
    public ApiResponse<AuthResponse> authenticate(@RequestParam AuthRequest authRequest) throws IOException {
        String token = authService.getToken(authRequest.getCode());
        GoogleAccount googleAccount = authService.getUserInfo(token);

        System.out.println("\n Token: " + token);
        System.out.println("Google Account: " + googleAccount);
        // xử lý gmail không thuộc tổ chức hcmut
        if (!"hcmut.edu.vn".equals(googleAccount.getHd())) {
            return ApiResponse.<AuthResponse>builder()
                    .code(409)
                    .message("Email không thuộc tổ chức HCMUT")
                    .build();
        }

        // Xử lý đăng nhập hoặc đăng ký user
        return ApiResponse.<AuthResponse>builder()
                .code(200)
                .message("Login successful")
                .result(
                        AuthResponse.builder()
                                .accessToken(token)
                                .user(userService.createUser(googleAccount))
                                .build())
                .build();
    }
}
