package com.teamup.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamup.main.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // Custom query methods can be defined here if needed
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}