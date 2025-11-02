package com.teamup.main.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String firstname;
    String lastname;

    @Size(min = 8, message = "PASSWORD_TOO_SHORT")
    String password;
    LocalDate dob;
}
