package com.teamup.main.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.teamup.main.enums.GroupStatus;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/*
 * tự viết bảng riêng cho mối quan hệ N-N giữa User và Group, lợi ích là có thể
 * thêm các thuộc tính khác, nhược điểm là phức tạp hơn
 */
@Entity
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class GroupMember {
    @EmbeddedId
    @JsonIgnore
    PairId id;

    @Enumerated(EnumType.STRING)
    GroupStatus status;
    String joinMessage;

    @ManyToOne
    @MapsId("firstId")
    @JsonIgnoreProperties({ "userTags", "groups" })
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Users user;

    @ManyToOne
    @MapsId("secondId")
    @JsonIgnoreProperties({ "groupTags", "groupMembers", "leaderId", "description", "maxMembers" })
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Groups group;
}