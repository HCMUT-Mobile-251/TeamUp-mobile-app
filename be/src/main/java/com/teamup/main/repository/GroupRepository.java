package com.teamup.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamup.main.model.Groups;

@Repository
public interface GroupRepository extends JpaRepository<Groups, String> {
    // Custom query methods can be defined here if needed
    List<Groups> findByTopicNameContainingIgnoreCase(String topicName);

    List<Groups> findByNameContainingIgnoreCase(String name);

    List<Groups> findByGroupClassContainingIgnoreCase(String groupClass);
}