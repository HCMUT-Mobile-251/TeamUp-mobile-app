package com.teamup.main.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class TagRequest {
    @NotBlank(message = "Tên tag không được để trống")
    String name;
}
