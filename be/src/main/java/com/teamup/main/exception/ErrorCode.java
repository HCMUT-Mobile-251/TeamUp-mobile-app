package com.teamup.main.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    UNKNOWN_ERROR("Lỗi không xác định", 500),

    EMAIL_ALREADY_EXISTS("Email đã tồn tại", 409),
    EMAIL_NOT_AUTHORITY("Email không thuộc tổ chức HCMUT", 409),
    INVALID_CREDENTIALS("Thông tin đăng nhập không hợp lệ", 401),
    USER_NOT_FOUND("Không tìm thấy người dùng", 404),
    GROUP_NOT_FOUND("Không tìm thấy nhóm", 404),
    COURSE_NOT_FOUND("Không tìm thấy khóa học", 404);

    String message;
    int code;
}