package com.teamup.main.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/*
* JsonIgnoreProperties để tránh lặp vô hạn khi lấy dữ liệu có quan hệ 2 chiều
 */
@Entity
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`groups`") // tránh trùng với từ khóa SQL
public class Groups {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String groupId;
    String name;
    String description;
    String groupClass;
    String topicName;
    int semester;
    int maxMembers;

    @ManyToOne
    Courses course;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Set<GroupTag> groupTags = new HashSet<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({ "group" })
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Set<GroupMember> groupMembers = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties({ "userTags", "groups", "studentId", "firstName", "lastName", "email", "phoneNumber",
            "faculty" })
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Users leaderId;
}
