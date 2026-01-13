package com.teamup.main.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreviewResponse {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String studentId;
}
