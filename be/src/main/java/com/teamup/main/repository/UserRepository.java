package com.teamup.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamup.main.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // Custom query methods can be defined here if needed
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
}