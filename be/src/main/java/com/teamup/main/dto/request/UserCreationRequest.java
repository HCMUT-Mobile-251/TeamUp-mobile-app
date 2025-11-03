package com.teamup.main.dto.request;

import lombok.Data;
import lombok.experimental.FieldDefaults;

//cho admin
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UserCreationRequest {
    String firstName;
    String lastName;
    String email;
}
