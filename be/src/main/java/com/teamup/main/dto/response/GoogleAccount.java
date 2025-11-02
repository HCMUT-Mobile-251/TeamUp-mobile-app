package com.teamup.main.dto.response;

import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
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
