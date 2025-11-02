package com.teamup.main.dto.request;

import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UserCreationRequest {
    String FirstName;
    String LastName;
    String Email;
}
