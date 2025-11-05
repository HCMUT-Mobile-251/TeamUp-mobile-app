package com.teamup.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamup.main.model.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    // Custom query methods can be defined here if needed
    List<Course> findByCourseIdContainingIgnoreCase(String courseId);

    List<Course> findByNameContainingIgnoreCase(String name);
}