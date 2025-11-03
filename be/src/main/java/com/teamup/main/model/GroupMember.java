package com.teamup.main.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class GroupMember {
    @EmbeddedId
    PairId id;

    String joinMessage;

    @ManyToOne
    @MapsId("firstId")   // trùng với field trong PairId!
    @JoinColumn(name = "userId")
    User user;

    @ManyToOne
    @MapsId("secondId")  // trùng với field trong PairId!
    @JoinColumn(name = "groupId")
    Group group;
}