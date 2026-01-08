package com.teamup.main.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class GoogleAccount {
    String id;
    String email;
    boolean verified_email;
    String name;
    String given_name;
    String family_name;
    String picture;
    String hd;
}
