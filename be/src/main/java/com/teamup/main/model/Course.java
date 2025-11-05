package com.teamup.main.model;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    String courseId;
    String name;

    // comment lại tránh vòng lặp vô hạn khi serializing, course ko cần biết có những group nào
    // @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    // Set<Group> groups = new HashSet<>();
}
