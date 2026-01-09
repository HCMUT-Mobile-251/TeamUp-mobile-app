package com.teamup.main.dto.response;

import com.fasterxml.jackson.annotation.JsonValue;
import com.teamup.main.enums.GroupStatus;

import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class NotificationResponse {
    @JsonValue
    GroupStatus status;
    String message;

    String nameUser;

    String groupId;
    String nameGroup;
}