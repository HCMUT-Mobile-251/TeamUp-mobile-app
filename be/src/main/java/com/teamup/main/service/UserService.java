package com.teamup.main.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamup.main.dto.request.GoogleAccount;
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

    //
    // User only
    //
    public List<User> getUsersByStudentId(String studentId) {
        return userRepository.findByStudentIdContainingIgnoreCase(studentId);
    }

    public User updateUser(String userId, UserUpdateRequest request) {
        User user = getUserById(userId);
        userMapper.updateUser(user, request);
        return userRepository.save(user);
    }
    
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    //
    // Admin only
    //
    public User createUser(GoogleAccount request) {
        java.util.Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isPresent()) {
            return getUserById(userOpt.get().getUserId());
        }

        User user = userMapper.toUser(request);
        return userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
}
