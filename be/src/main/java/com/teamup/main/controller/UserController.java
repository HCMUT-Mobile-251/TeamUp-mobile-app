package com.teamup.main.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.teamup.main.dto.request.GoogleAccount;
import com.teamup.main.dto.request.UserCreationRequest;
import com.teamup.main.dto.request.UserDeleteRequest;
import com.teamup.main.dto.request.UserUpdateRequest;
import com.teamup.main.dto.response.ApiResponse;
import com.teamup.main.model.User;
import com.teamup.main.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /*
     * User only
     */
    @PutMapping("/{userId}")
    public ApiResponse<User> updateUser(@PathVariable String userId, @RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<User>builder()
                .code(200)
                .message("Cập nhật người dùng thành công")
                .result(userService.updateUser(userId, request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<User>> getUsersByStudentId(@RequestParam String studentId) {
        return ApiResponse.<List<User>>builder()
                .code(200)
                .message("Lấy danh sách người dùng thành công")
                .result(userService.getUsersByStudentId(studentId))
                .build();
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<Void> deleteUser(@PathVariable UserDeleteRequest userDeleteRequest) {
        userService.deleteUser(userDeleteRequest.getUserId());
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Xóa người dùng thành công")
                .build();
    }

    /*
     * Admin only
     */
    @PostMapping("/admin")
    public ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request) {
        GoogleAccount googleAccount = GoogleAccount.builder()
                .email(request.getEmail())
                .given_name(request.getFirstName())
                .family_name(request.getLastName())
                .build();
        return ApiResponse.<User>builder()
                .code(200)
                .message("Tạo người dùng thành công")
                .result(userService.createUser(googleAccount))
                .build();
    }

    @GetMapping("/admin/all")
    public ApiResponse<List<User>> getUsers() {
        return ApiResponse.<List<User>>builder()
                .code(200)
                .message("Lấy danh sách người dùng thành công")
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/admin/{userId}")
    public ApiResponse<User> getUsersId(@PathVariable String userId) {
        return ApiResponse.<User>builder()
                .code(200)
                .message("Lấy người dùng thành công")
                .result(userService.getUserById(userId))
                .build();
    }
}
