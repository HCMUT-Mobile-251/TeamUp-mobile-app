package com.teamup.main.enums;

// import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum GroupStatus {
    PENDING_APPROVAL("Chờ chấp nhận tham gia!"), // Chờ chập nhận tham gia
    WAITING_APPROVAL("Chờ được chấp nhận!"), // Chờ leader duyệt
    JOINED("Đã tham gia!"),
    // NOT_JOINED("Chưa tham gia!"),
    REJECTED("Bị từ chối!"),
    LEFT("Đã rời nhóm!"),
    REMOVED("Bị loại khỏi nhóm!"),

    // trường hợp khác
    ADD_MEMBER("Được leader mời!"),
    CREATE_GROUP("Nhóm được tạo bởi nhóm trưởng!");

    String description;

    // @JsonValue - Removed to allow Enum Name serialization
    public String getDescription() {
        return description;
    }
}
