package com.teamup.main.dto.response;

import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UserResponse {
    String userId;
    String studentId;
    String firstName;
    String lastName;
    String fullName;
    String email;
    String phoneNumber;
    String faculty;
}
