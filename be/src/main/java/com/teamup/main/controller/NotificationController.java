package com.teamup.main.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.teamup.main.dto.response.ApiResponse;
import com.teamup.main.model.GroupMember;
import com.teamup.main.model.PairId;
import com.teamup.main.service.NotificationService;

import jakarta.validation.Valid;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/notification")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class NotificationController {
    @Autowired
    NotificationService notificationService;

    @GetMapping("/{userId}")
    public ApiResponse<List<GroupMember>> getNotifications(@PathVariable String userId) {
        return ApiResponse.<List<GroupMember>>builder()
                .code(200)
                .message("Get notifications successfully")
                .result(notificationService.getNotificationByUserId(userId, 0, 20).getContent())
                .build();
    }

    @GetMapping("/{userId}/search")
    public ApiResponse<List<GroupMember>> findNotificationByName(@PathVariable String userId, @RequestParam String search) {
        return ApiResponse.<List<GroupMember>>builder()
                .code(200)
                .message("Find notifications successfully")
                .result(notificationService.findNotificationByNameByUserId(userId, search, 0, 20).getContent())
                .build();
    }

    @DeleteMapping("/delete")
    public ApiResponse<Void> deleteNotification(@RequestBody @Valid PairId pairId) {
        System.out.println(pairId);
        notificationService.deleteNotification(pairId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Delete notifications successfully")
                .build();
    }
}
