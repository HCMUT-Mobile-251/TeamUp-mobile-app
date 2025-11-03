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
public class GroupTag {
    @EmbeddedId
    PairId id;

    @ManyToOne
    @MapsId("firstId")
    @JoinColumn(name = "groupId")
    Group group;

    @ManyToOne
    @MapsId("secondId")
    @JoinColumn(name = "tagId")
    Tag tag;
} 