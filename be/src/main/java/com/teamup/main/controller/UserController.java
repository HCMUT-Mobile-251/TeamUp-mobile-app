package com.teamup.main.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.teamup.main.dto.request.GoogleAccount;
import com.teamup.main.dto.request.UserCreationRequest;
import com.teamup.main.dto.request.UserUpdateRequest;
import com.teamup.main.dto.response.ApiResponse;
import com.teamup.main.model.Tags;
import com.teamup.main.model.Users;
import com.teamup.main.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /*
     * User only
     */
    @PatchMapping("/{userId}")
    public ApiResponse<Users> updateUser(@PathVariable String userId, @RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<Users>builder()
                .code(200)
                .message("Cập nhật người dùng thành công")
                .result(userService.updateUser(userId, request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<Users>> getUsersByStudentId(@RequestParam String studentId) {
        return ApiResponse.<List<Users>>builder()
                .code(200)
                .message("Lấy danh sách người dùng thành công")
                .result(userService.getUsersByStudentId(studentId))
                .build();
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Xóa người dùng thành công")
                .build();
    }

    @GetMapping("/{userId}")
    public ApiResponse<Users> getUserById(@PathVariable String userId) {
        return ApiResponse.<Users>builder()
                .code(200)
                .message("Lấy người dùng thành công")
                .result(userService.findById(userId))
                .build();
    }

    @PatchMapping("/{userId}/tags")
    public ApiResponse<Void> updateUserTag(@PathVariable String userId, @RequestBody @Valid List<Tags> listTag) {
        userService.updateUserTag(userId, listTag);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("User updated in group successfully")
                .build();
    }

    /*
     * Admin only
     */
    @PostMapping("/admin")
    public ApiResponse<Users> createUser(@RequestBody @Valid UserCreationRequest request) {
        // transform to GoogleAccount
        GoogleAccount googleAccount = GoogleAccount.builder()
                .email(request.getEmail())
                .given_name(request.getFirstName())
                .family_name(request.getLastName())
                .build();
        return ApiResponse.<Users>builder()
                .code(200)
                .message("Tạo người dùng thành công")
                .result(userService.createUser(googleAccount))
                .build();
    }

    @GetMapping("/admin/all")
    public ApiResponse<List<Users>> getUsers() {
        return ApiResponse.<List<Users>>builder()
                .code(200)
                .message("Lấy danh sách người dùng thành công")
                .result(userService.getUsers())
                .build();
    }

}
