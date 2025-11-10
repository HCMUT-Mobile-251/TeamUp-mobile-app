package com.teamup.main.dto.response;

import java.util.Set;

import com.teamup.main.model.Courses;
import com.teamup.main.model.GroupTag;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class GroupResponse {
    String groupId;
    String name;
    String groupClass;
    String topicName;

    Courses course;
    Set<GroupTag> groupTags;
}
