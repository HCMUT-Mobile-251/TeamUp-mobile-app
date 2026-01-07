package com.teamup.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.teamup.main.dto.request.GoogleAccount;
import com.teamup.main.dto.response.ApiResponse;
import com.teamup.main.dto.response.AuthResponse;
import com.teamup.main.service.AuthService;
import com.teamup.main.service.UserService;

import lombok.experimental.FieldDefaults;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/auth")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AuthController {
    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;

    @GetMapping("/login")
    public RedirectView authenticate(
            @RequestParam String code,
            @RequestParam(required = false) String state) throws IOException {
        try {
            String token = authService.getToken(code);
            GoogleAccount googleAccount = authService.getUserInfo(token);

            System.out.println("\n Token: " + token);
            System.out.println("Google Account: " + googleAccount);
            System.out.println("State parameter: " + state);

            // xử lý gmail không thuộc tổ chức hcmut
            if (!"hcmut.edu.vn".equals(googleAccount.getHd())) {
                String errorMessage = URLEncoder.encode("Email không thuộc tổ chức HCMUT", StandardCharsets.UTF_8.toString());
                String stateParam = state != null ? "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8.toString()) : "";
                return new RedirectView("/auth-redirect.html?error=" + errorMessage + stateParam);
            }

            // Xử lý đăng nhập hoặc đăng ký user
            AuthResponse authResponse = AuthResponse.builder()
                    .accessToken(token)
                    .user(userService.createUser(googleAccount))
                    .build();

            // Encode token và userId để truyền qua URL
            String encodedToken = URLEncoder.encode(authResponse.getAccessToken(), StandardCharsets.UTF_8.toString());
            String encodedUserId = URLEncoder.encode(authResponse.getUser().getUserId(), StandardCharsets.UTF_8.toString());

            // Thêm state parameter nếu có (chứa redirectUri từ app)
            String stateParam = state != null ? "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8.toString()) : "";

            // Redirect về auth-redirect.html với token, userId và state
            return new RedirectView("/auth-redirect.html?token=" + encodedToken + "&userId=" + encodedUserId + stateParam);

        } catch (Exception e) {
            System.err.println("Auth error: " + e.getMessage());
            e.printStackTrace();
            String errorMessage = URLEncoder.encode("Đăng nhập thất bại: " + e.getMessage(), StandardCharsets.UTF_8.toString());
            String stateParam = state != null ? "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8.toString()) : "";
            return new RedirectView("/auth-redirect.html?error=" + errorMessage + stateParam);
        }
    }

    @GetMapping("/{token}")
    public ApiResponse<Boolean> verifyAccessToken(@PathVariable String token) throws IOException {
        return authService.verifyAccessToken(token);
        // return authService.getUserInfo(token);
    }

}
