package com.teamup.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamup.main.dto.request.AuthRequest;
import com.teamup.main.dto.response.ApiResponse;
import com.teamup.main.dto.response.AuthResponse;
import com.teamup.main.service.AuthService;

import java.net.Authenticator;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;

    @PostMapping("/login")
    public ApiResponse<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        boolean result = authService.authenticate(request);

        return ApiResponse.<AuthResponse>builder()
        .result(AuthResponse.builder().build())
        .build();
    }
    
}
