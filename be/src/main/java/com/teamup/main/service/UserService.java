package com.teamup.main.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.teamup.main.dto.request.UserCreationRequest;
import com.teamup.main.dto.request.UserUpdateRequest;
import com.teamup.main.exception.AppException;
import com.teamup.main.exception.ErrorCode;
import com.teamup.main.mapper.UserMapper;
import com.teamup.main.model.User;
import com.teamup.main.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public User createRequest(UserCreationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = userMapper.toUser(request);
        // PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public User updateUser(String userId, UserUpdateRequest request) {
        User user = getUserById(userId);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        userMapper.updateUser(user, request);
        return userRepository.save(user);
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
}
