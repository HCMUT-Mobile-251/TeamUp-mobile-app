package com.teamup.main.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamup.main.exception.AppException;
import com.teamup.main.exception.ErrorCode;
import com.teamup.main.model.Courses;
import com.teamup.main.repository.CourseRepository;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    /*
     * User only
     */
    public List<Courses> getCourse(String course) {
        List<Courses> byId = courseRepository.findByCourseIdContainingIgnoreCase(course.trim());
        if (!byId.isEmpty()) {
            return byId;
        }
        List<Courses> byName = courseRepository.findByNameContainingIgnoreCase(course.trim());
        return byName;
    }

    /*
     * Admin only
     */
    public List<Courses> createCourse(List<Courses> courses) {
        return courseRepository.saveAll(courses);
    }

    public Courses updateCourse(Courses course) {
        Courses existingCourse = findCourse(course.getCourseId());
        if (existingCourse != null) {
            return courseRepository.save(course);
        }
        throw new AppException(ErrorCode.COURSE_NOT_FOUND);
    }

    public List<Courses> getCourses() {
        return courseRepository.findAll();
    }

    public Courses findCourse(String courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
    }

    public void deleteCourse(String courseId) {
        findCourse(courseId);
        courseRepository.deleteById(courseId);
    }
}
