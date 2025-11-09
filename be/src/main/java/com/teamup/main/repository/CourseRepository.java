package com.teamup.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamup.main.model.Courses;

@Repository
public interface CourseRepository extends JpaRepository<Courses, String> {
    // Custom query methods can be defined here if needed
    List<Courses> findByCourseIdContainingIgnoreCase(String courseId);

    List<Courses> findByNameContainingIgnoreCase(String name);
}