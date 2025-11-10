package com.teamup.main.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.teamup.main.dto.request.SearchRequest;
import com.teamup.main.dto.response.ApiResponse;
import com.teamup.main.dto.response.GroupResponse;
import com.teamup.main.service.SearchService;

import jakarta.validation.Valid;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/search")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class SearchController {
    @Autowired
    SearchService searchService;

    @GetMapping("/normal")
    public ApiResponse<List<GroupResponse>> normalSearchGroups(@RequestParam String group) {
        return ApiResponse.<List<GroupResponse>>builder()
                .code(200)
                .message("Search completed successfully")
                .result(searchService.normalSearchGroup(group))
                .build();
    }

    @GetMapping("/advance")
    public ApiResponse<List<GroupResponse>> advanceSearchGroups(@RequestBody @Valid SearchRequest request) {
        return ApiResponse.<List<GroupResponse>>builder()
                .code(200)
                .message("Search completed successfully")
                .result(searchService.advanceSearchGroups(request))
                .build();
    }
}
