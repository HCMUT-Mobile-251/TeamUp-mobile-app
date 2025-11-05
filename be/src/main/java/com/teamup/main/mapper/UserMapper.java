package com.teamup.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.teamup.main.dto.request.GoogleAccount;
import com.teamup.main.dto.request.UserUpdateRequest;
import com.teamup.main.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "faculty", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "userTags", ignore = true)
    @Mapping(target = "groupMembers", ignore = true)
    @Mapping(source = "given_name", target = "firstName")
    @Mapping(source = "family_name", target = "lastName")
    User toUser(GoogleAccount request);
    
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "userTags", ignore = true)
    @Mapping(target = "groupMembers", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}