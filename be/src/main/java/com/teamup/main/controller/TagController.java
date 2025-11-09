package com.teamup.main.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.teamup.main.dto.response.ApiResponse;
import com.teamup.main.model.Tags;
import com.teamup.main.service.TagService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/tag")
public class TagController {
    @Autowired
    private TagService tagService;

    /*
     * User only
     */
    @GetMapping
    public ApiResponse<List<Tags>> getTagByName(@RequestParam String search) {
        return ApiResponse.<List<Tags>>builder()
                .code(200)
                .message("Lấy tag thành công")
                .result(tagService.getTagByName(search))
                .build();
    }

    /*
     * Admin only
     */
    @PostMapping("/admin")
    public ApiResponse<List<Tags>> createTag(@RequestBody @Valid List<Tags> request) {
        return ApiResponse.<List<Tags>>builder()
                .code(200)
                .message("Tag created successfully")
                .result(tagService.createTag(request))
                .build();
    }

    @DeleteMapping("/admin/{tagId}")
    public ApiResponse<Tags> deleteTag(@PathVariable String tagId) {
        tagService.deleteTag(tagId);
        return ApiResponse.<Tags>builder()
                .code(200)
                .message("Tag deleted successfully")
                .build();
    }

    @GetMapping("/admin/all")
    public List<Tags> getTags() {
        return tagService.getTags();
    }
}
