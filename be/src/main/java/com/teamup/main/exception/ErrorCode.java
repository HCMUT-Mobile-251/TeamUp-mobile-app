package com.teamup.main.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    USER_NOT_FOUND("User not found", 404),
    USERNAME_ALREADY_EXISTS("Username exists", 409),
    PASSWORD_TOO_SHORT("Password must be at least 8 characters", 410),
    INVALID_CREDENTIALS("Invalid credentials", 401),
    UNKNOWN_ERROR("Unknown error", 500);

    String message;
    int code;
}
