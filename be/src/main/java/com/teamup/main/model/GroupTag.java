package com.teamup.main.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class GroupTag {
    @EmbeddedId
    @JsonIgnore
    PairId id;
    
    @ManyToOne
    @MapsId("firstId")
    @JsonIgnore
    Groups group;

    @ManyToOne
    @MapsId("secondId")
    Tags tag;
}