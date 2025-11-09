package com.teamup.main.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    @JsonIgnore
    PairId id;

    @ManyToOne
    @MapsId("firstId")
    @JoinColumn(name = "user_id")
    @JsonIgnore
    Users user;

    @ManyToOne
    @MapsId("secondId")
    @JoinColumn(name = "group_id")
    @JsonIgnore
    Tags tag;
}