package com.teamup.main.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.teamup.main.dto.request.GroupRequest;
import com.teamup.main.dto.request.JoinRequest;
import com.teamup.main.dto.response.ApiResponse;
import com.teamup.main.dto.response.GroupResponse;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @PatchMapping("/{groupId}")
    public ApiResponse<Groups> updateGroup(@RequestBody @Valid GroupRequest request) {
        return ApiResponse.<Groups>builder()
                .code(200)
                .message("Group updated successfully")
                .result(groupService.updateGroup(request))
                .build();
    }

    @PatchMapping("/{groupId}/tags")
    public ApiResponse<Void> updateGroupTag(@PathVariable String groupId, @RequestBody @Valid List<Tags> listTag) {
        groupService.updateGroupTag(groupId, listTag);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Group updated successfully")
                .build();
    }

    @DeleteMapping("/{groupId}")
    public ApiResponse<Void> deleteGroup(@PathVariable String groupId) {
        groupService.deleteGroup(groupId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Group deleted successfully")
                .build();
    }

    @GetMapping("/{groupId}/members")
    public ApiResponse<List<UserResponse>> getMembers(@PathVariable String groupId) {
        return ApiResponse.<List<UserResponse>>builder()
                .code(200)
                .message("Get members successfully")
                .result(groupService.getMembers(groupId))
                .build();
    }

    @GetMapping("/{groupId}")
    public ApiResponse<Groups> getGroupById(@PathVariable String groupId) {
        return ApiResponse.<Groups>builder()
                .code(200)
                .message("Get members successfully")
                .result(groupService.findGroup(groupId))
                .build();
    }

    @PatchMapping("/{groupId}/decrease")
    public ApiResponse<Void> kickMemberGroup(@PathVariable String groupId, @RequestParam String userId) {
        groupService.kickOrOutGroup(groupId, userId, true);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Removed from group successfully")
                .build();
    }

    @PatchMapping("/{groupId}/out")
    public ApiResponse<Void> outGroup(@PathVariable String groupId, @RequestParam String userId) {
        groupService.kickOrOutGroup(groupId, userId, false);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Removed from group successfully")
                .build();
    }

    @PatchMapping("/{groupId}/increase")
    public ApiResponse<Void> addMember(@PathVariable String groupId, @RequestBody @Valid List<String> listUserId) {
        groupService.addMember(groupId, listUserId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Invite member to group successfully")
                .build();
    }

    @PatchMapping("/{groupId}/join")
    public ApiResponse<Void> joinRequest(@PathVariable String groupId, @RequestBody @Valid JoinRequest request) {
        groupService.joinRequest(groupId, request.getUserId(), request.getMessage());
        return ApiResponse.<Void>builder()
                .code(200)
                .message("User sent join request successfully")
                .build();
    }

    @PatchMapping("/{groupId}/reject")
    public ApiResponse<Void> rejectJoinRequest(@PathVariable String groupId, @RequestParam String userId) {
        groupService.rejectJoinRequest(groupId, userId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("User rejected group successfully")
                .build();
    }

    @PatchMapping("/{groupId}/accept")
    public ApiResponse<Void> acceptJoinRequest(@PathVariable String groupId, @RequestParam String userId) {
        groupService.acceptJoinRequest(groupId, userId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("User joined group successfully")
                .build();
    }

    // đề xuất cho từng user và theo tag
    @GetMapping("/suggest/{userId}")
    public ApiResponse<List<GroupResponse>> getIndividualGroups(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @PathVariable String userId) {
        return ApiResponse.<List<GroupResponse>>builder()
                .code(200)
                .message("Get individual groups successfully")
                .result(groupService.getIndividualGroups(page, size, userId))
                .build();
    }

    /*
     * Admin only
     */
    @GetMapping("/admin/all")
    public ApiResponse<List<Groups>> getGroups(@RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "200") Integer size) {
        return ApiResponse.<List<Groups>>builder()
                .code(200)
                .message("Get all groups successfully")
                .result(groupService.getGroups(page, size))
                .build();
    }

    // @GetMapping("/admin/semester")
    // public List<Group> getGroupSemesters(@RequestParam String semester) {
    // return groupService.getGroupSemesters(semester);
    // }
}
