package com.teamup.main.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String groupId;
    String name;
    String description;
    String groupClass;
    Number semester;
    Number maxMembers;
    
    @OneToMany(mappedBy = "group")
    Set<GroupMember> groupMembers = new HashSet<>();
    
    @OneToMany(mappedBy = "group")
    Set<GroupTag> groupTags = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "courseId", insertable = false, updatable = false)
    Course course;
    
    @ManyToOne
    @JoinColumn(name = "leaderId", insertable = false, updatable = false)
    User user;
}
