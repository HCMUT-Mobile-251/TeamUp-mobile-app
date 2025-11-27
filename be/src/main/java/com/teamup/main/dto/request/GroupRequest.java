package com.teamup.main.dto.request;

import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class GroupRequest {
    String groupId;
    String name;
    String description;
    String groupClass;
    String topicName;
    int maxMembers;

    String leaderId;
    String courseId;
}
