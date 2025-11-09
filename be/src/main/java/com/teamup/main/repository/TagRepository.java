package com.teamup.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamup.main.model.Tags;

@Repository
public interface TagRepository extends JpaRepository<Tags, String> {
    // Custom query methods can be defined here if needed
    List<Tags> findByNameContainingIgnoreCase(String name);
}