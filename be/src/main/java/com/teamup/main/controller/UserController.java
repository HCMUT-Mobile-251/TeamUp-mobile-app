package com.teamup.main.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.teamup.main.dto.request.UserCreationRequest;
import com.teamup.main.dto.request.UserUpdateRequest;
import com.teamup.main.dto.response.ApiResponse;
import com.teamup.main.model.User;
import com.teamup.main.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request) {
        ApiResponse<User> response = new ApiResponse<>();
        response.setResult(userService.createRequest(request));
        return response;
    }

    @PutMapping("/{userId}")
    public ApiResponse<User> updateUser(@PathVariable String userId, @RequestBody @Valid UserUpdateRequest request) {
        ApiResponse<User> response = new ApiResponse<>();
        response.setResult(userService.updateUser(userId, request));
        return response;
    }
    
    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public User getUsersId(@PathVariable String userId) {
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
    }
}
