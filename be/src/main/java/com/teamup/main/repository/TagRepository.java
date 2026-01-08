package com.teamup.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.teamup.main.model.Tags;

@Repository
public interface TagRepository extends JpaRepository<Tags, String> {
    // Custom query methods can be defined here if needed
    List<Tags> findByTagNameContainingIgnoreCase(String tagName);

    @Query(value = "SELECT * FROM tags ORDER BY RAND() LIMIT :n", nativeQuery = true)
    List<Tags> findRandomTags(@Param("n") int n);
}