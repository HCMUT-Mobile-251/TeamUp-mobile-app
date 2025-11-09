package com.teamup.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamup.main.model.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, String> {
    // Custom query methods can be defined here if needed
    java.util.List<Users> findByStudentId(String studentId);
    java.util.List<Users> findByStudentIdContainingIgnoreCase(String studentId);
    Optional<Users> findByEmail(String email);
}