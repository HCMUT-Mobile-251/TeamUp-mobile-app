package com.teamup.main.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

// Cách 1 tạo class có constructor nhận object
// Cách 2 truyền thằng key-enum xem ở file GlobalExceptionHandler
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppException extends RuntimeException {
    ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
