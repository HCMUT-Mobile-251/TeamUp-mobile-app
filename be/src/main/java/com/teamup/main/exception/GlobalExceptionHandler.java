package com.teamup.main.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.teamup.main.dto.response.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    // bắt các error khác
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception exception) {
        ErrorCode errorCode = ErrorCode.UNKNOWN_ERROR;
        ApiResponse<?> response = new ApiResponse<>();
        response.setMessage(errorCode.getMessage());
        response.setCode(errorCode.getCode());
        return ResponseEntity.internalServerError().body(response);
    }

    // Cách 1 dùng AppException
    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse<?>> handleAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse<?> response = new ApiResponse<>();
        response.setMessage(errorCode.getMessage());
        response.setCode(errorCode.getCode());
        return ResponseEntity.badRequest().body(response);
    }

    // Cách 2 truyền key enum nhưng phải handle mapping sai
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException exception) {
        FieldError temp = exception.getFieldError();
        String enumKey = "UNKNOWN_ERROR";
        if (temp != null) {
            enumKey = temp.getDefaultMessage();
        }
        ErrorCode errorCode = ErrorCode.valueOf(enumKey);

        ApiResponse<?> response = new ApiResponse<>();
        response.setMessage(errorCode.getMessage());
        response.setCode(errorCode.getCode());
        return ResponseEntity.badRequest().body(response);
    }
}
