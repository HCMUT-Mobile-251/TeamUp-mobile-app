package com.teamup.main.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.teamup.main.configuration.SecurityConfig;
import com.teamup.main.dto.request.AuthRequest;
import com.teamup.main.exception.AppException;
import com.teamup.main.exception.ErrorCode;
import com.teamup.main.repository.UserRepository;

import lombok.Data;

@Service
@Data
public class AuthService {
    private UserRepository userRepository;

    public boolean authenticate(AuthRequest request) {
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        SecurityConfig securityConfig = new SecurityConfig();
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        return passwordEncoder.matches(request.getPassword(), user.getPassword());
    }
}
