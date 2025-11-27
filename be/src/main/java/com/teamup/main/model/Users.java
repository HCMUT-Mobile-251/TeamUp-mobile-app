package com.teamup.main.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String userId;
    String studentId;
    String firstName;
    String lastName;
    String email;
    String phoneNumber;
    String faculty;

    // phải trỏ đúng tên mappedBy biến bên GroupMember
    // dùng set tranh trùng lặp
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({ "joinMessage", "user" })
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Set<GroupMember> groups = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Set<UserTag> userTags = new HashSet<>();

    public void addUserTag(UserTag userTag) {
        userTags.add(userTag);
        userTag.setUser(this);
    }

    public void removeUserTag(UserTag userTag) {
        userTags.remove(userTag);
        userTag.setUser(null);
    }

    public void addGroupMember(GroupMember groupMember) {
        groups.add(groupMember);
        groupMember.setUser(this);
    }

    public void removeGroupMember(GroupMember groupMember) {
        groups.remove(groupMember);
        groupMember.setUser(null);
    }
}
