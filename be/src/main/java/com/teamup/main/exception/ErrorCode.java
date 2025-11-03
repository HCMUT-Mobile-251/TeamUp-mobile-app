package com.teamup.main.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    USER_NOT_FOUND("Không tìm thấy người dùng", 404),
    EMAIL_ALREADY_EXISTS("Email đã tồn tại", 409),
    EMAIL_NOT_AUTHORITY("Email không thuộc tổ chức HCMUT", 409),
    PASSWORD_TOO_SHORT("Mật khẩu phải có ít nhất 8 ký tự", 410),
    INVALID_CREDENTIALS("Thông tin đăng nhập không hợp lệ", 401),
    UNKNOWN_ERROR("Lỗi không xác định", 500);

    String message;
    int code;
}
