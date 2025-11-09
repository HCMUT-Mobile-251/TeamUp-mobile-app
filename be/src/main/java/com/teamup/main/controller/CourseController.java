package com.teamup.main.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.teamup.main.dto.response.ApiResponse;
import com.teamup.main.model.Courses;
import com.teamup.main.service.CourseService;

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
@RequestMapping("/course")
public class CourseController {
    @Autowired
    private CourseService courseService;

    /*
     * User only
     */
    @GetMapping
    public ApiResponse<List<Courses>> getCourseById(@RequestParam String search) {
        return ApiResponse.<List<Courses>>builder()
                .code(200)
                .message("Lấy khóa học thành công")
                .result(courseService.getCourseById(search))
                .build();
    }

    /*
     * Admin only
     */
    @PostMapping("/admin")
    public ApiResponse<List<Courses>> createCourse(@RequestBody @Valid List<Courses> request) {
        return ApiResponse.<List<Courses>>builder()
                .code(200)
                .message("Tạo khóa học thành công")
                .result(courseService.createCourse(request))
                .build();
    }

    @PutMapping("/admin/{courseId}")
    public ApiResponse<Courses> updateCourse(@RequestBody @Valid Courses request) {
        return ApiResponse.<Courses>builder()
                .code(200)
                .message("Cập nhật khóa học thành công")
                .result(courseService.updateCourse(request))
                .build();
    }

    @GetMapping("/admin/all")
    public ApiResponse<List<Courses>> getCourses() {
        return ApiResponse.<List<Courses>>builder()
                .code(200)
                .message("Lấy danh sách khóa học thành công")
                .result(courseService.getCourses())
                .build();
    }

    @DeleteMapping("/admin/{courseId}")
    public ApiResponse<Void> deleteCourse(@PathVariable String courseId) {
        courseService.deleteCourse(courseId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Xóa khóa học thành công")
                .build();
    }
}
