package com.teamup.main.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.experimental.FieldDefaults;

@Entity
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UserTag {
    @EmbeddedId
    PairId id;

    @ManyToOne
    @MapsId("firstId")
    @JoinColumn(name = "userId")
    User user;

    @ManyToOne
    @MapsId("secondId")
    @JoinColumn(name = "tagId")
    Tag tag;
} 