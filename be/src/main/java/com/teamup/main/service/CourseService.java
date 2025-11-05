package com.teamup.main.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamup.main.exception.AppException;
import com.teamup.main.exception.ErrorCode;
import com.teamup.main.model.Course;
import com.teamup.main.repository.CourseRepository;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    /*
     * User only
     */
    public List<Course> getCourseById(String course) {
        List<Course> byId = courseRepository.findByCourseIdContainingIgnoreCase(course);
        if (!byId.isEmpty()) {
            return byId;
        }
        List<Course> byName = courseRepository.findByNameContainingIgnoreCase(course);
        return byName;
    }

    /*
     * Admin only
     */
    public List<Course> createCourse(List<Course> courses) {
        return courseRepository.saveAll(courses);
    }

    public Course updateCourse(String courseId, Course course) {
        if (courseRepository.existsById(courseId)) {
            course.setCourseId(courseId);
            return courseRepository.save(course);
        }
        throw new AppException(ErrorCode.COURSE_NOT_FOUND);
    }

    public List<Course> getCourses() {
        return courseRepository.findAll();
    }

    public void deleteCourse(String courseId) {
        courseRepository.deleteById(courseId);
    }
}
