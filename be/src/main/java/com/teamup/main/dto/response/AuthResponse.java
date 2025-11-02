package com.teamup.main.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AuthResponse {
    String accessToken;
    GoogleAccount googleAccount;
}
