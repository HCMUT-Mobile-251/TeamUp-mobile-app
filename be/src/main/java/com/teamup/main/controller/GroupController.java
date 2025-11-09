package com.teamup.main.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.teamup.main.dto.request.GroupRequest;
import com.teamup.main.dto.response.ApiResponse;
import com.teamup.main.dto.response.UserResponse;
import com.teamup.main.model.Groups;
import com.teamup.main.model.Tags;
import com.teamup.main.service.GroupService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/group")
public class GroupController {
    @Autowired
    private GroupService groupService;

    /*
     * User only
     */
    @PostMapping
    public ApiResponse<Groups> createGroup(@RequestBody @Valid GroupRequest request) {
        return ApiResponse.<Groups>builder()
                .code(200)
                .message("Group created successfully")
                .result(groupService.createGroup(request))
                .build();
    }

    // có thể switch leader
    @PutMapping("/{groupId}")
    public ApiResponse<Groups> updateGroup(@RequestBody @Valid GroupRequest request) {
        return ApiResponse.<Groups>builder()
                .code(200)
                .message("Group updated successfully")
                .result(groupService.updateGroup(request))
                .build();
    }

    @PutMapping("/{groupId}/tag")
    public ApiResponse<Void> updateGroupTag(@PathVariable String groupId, @RequestBody @Valid Tags tag) {
        groupService.updateGroupTag(groupId, tag);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Group updated successfully")
                .build();
    }

    @DeleteMapping("/{groupId}")
    public ApiResponse<Groups> deleteGroup(@PathVariable String groupId) {
        groupService.deleteGroup(groupId);
        return ApiResponse.<Groups>builder()
                .code(200)
                .message("Group deleted successfully")
                .build();
    }

    @GetMapping("/member/{groupId}")
    public ApiResponse<List<UserResponse>> getMembers(@PathVariable String groupId) {
        List<UserResponse> members = groupService.getMembers(groupId);
        return ApiResponse.<List<UserResponse>>builder()
                .code(200)
                .message("Get members successfully")
                .result(members)
                .build();
    }

    @PutMapping("/decrease/{groupId}")
    public ApiResponse<Void> kickOrOutGroup(@PathVariable String groupId, @RequestParam String userId) {
        groupService.kickOrOutGroup(groupId, userId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Removed from group successfully")
                .build();
    }

    @PutMapping("/increase/{groupId}")
    public ApiResponse<Void> addMember(@PathVariable String groupId, @RequestBody @Valid List<String> listUserId) {
        groupService.addMember(groupId, listUserId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("User added to group successfully")
                .build();
    }

    /*
     * Admin only
     */
    @GetMapping("/admin/all")
    public List<Groups> getGroups() {
        return groupService.getGroups(0, 20);
    }

    // @GetMapping("/admin/semester")
    // public List<Group> getGroupSemesters(@RequestParam String semester) {
    // return groupService.getGroupSemesters(semester);
    // }
}
