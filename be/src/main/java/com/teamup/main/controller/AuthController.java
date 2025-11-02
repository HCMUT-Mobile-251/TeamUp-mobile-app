package com.teamup.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamup.main.dto.response.ApiResponse;
import com.teamup.main.dto.response.AuthResponse;
import com.teamup.main.dto.response.GoogleAccount;
import com.teamup.main.service.AuthService;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public ApiResponse<AuthResponse> authenticate(@RequestParam String code) throws IOException {
        String token = authService.getToken(code);
        GoogleAccount googleAccount = authService.getUserInfo(token);
        System.out.println("\n Token: " + token);
        System.out.println("Google Account: " + googleAccount);
        
        return ApiResponse.<AuthResponse>builder()
                .result(AuthResponse.builder().accessToken(token).googleAccount(googleAccount).build())
                .build();
    }
}
