package com.teamup.main.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AuthRequest {
    String username;

    @Size(min = 8, message = "PASSWORD_TOO_SHORT")
    String password;
}
