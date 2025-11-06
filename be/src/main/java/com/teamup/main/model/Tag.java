package com.teamup.main.model;

// import java.util.HashSet;
// import java.util.Set;
// import jakarta.persistence.OneToMany;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String tagId;
    String name;

    // comment lại tránh vòng lặp vô hạn khi serializing, tag ko cần biết có những user/tag nào
    // @OneToMany(mappedBy = "tag")
    // Set<UserTag> userTags = new HashSet<>();

    // @OneToMany(mappedBy = "tag")
    // Set<GroupTag> groupTags = new HashSet<>();
}
