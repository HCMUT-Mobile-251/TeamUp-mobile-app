package com.teamup.main.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String userId;
    String studentId;
    String firstName;
    String lastName;
    String email;
    String phoneNumber;
    String faculty;

    @OneToMany(mappedBy = "user") // phải trỏ đúng tên biến bên GroupMember
    Set<GroupMember> groupMembers = new HashSet<>();

    @OneToMany(mappedBy = "user")
    Set<UserTag> userTags = new HashSet<>();
}
