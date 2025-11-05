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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`group`") 
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String groupId;
    String name;
    String description;
    String groupClass;
    String topicName;
    Number semester;
    Number maxMembers;
    
    @OneToMany(mappedBy = "group")
    Set<GroupMember> groupMembers = new HashSet<>();
    
    @OneToMany(mappedBy = "group")
    Set<GroupTag> groupTags = new HashSet<>();

    // Hibernate vẫn lưu ID
    // group.getUser() ban đầu chỉ là lazy proxy, chỉ query khi thật sự cần. User đó đã từng được load trong cùng Session
    // nói chung là tối ưu hiệu năng hơn so với việc chỉ lưu String userId
    @ManyToOne
    @JoinColumn(name = "courseId", insertable = false, updatable = false)
    Course course;
    
    @ManyToOne
    @JoinColumn(name = "leaderId", insertable = false, updatable = false)
    User user;
}
