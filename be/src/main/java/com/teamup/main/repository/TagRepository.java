package com.teamup.main.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.teamup.main.model.Tags;

@Repository
public interface TagRepository extends JpaRepository<Tags, String> {
    // Custom query methods can be defined here if needed
    List<Tags> findByNameContainingIgnoreCase(String name);

    @Query(value = "SELECT * FROM tags ORDER BY RAND()", nativeQuery = true)
    List<Tags> findRandomTags(Pageable pageable);
}