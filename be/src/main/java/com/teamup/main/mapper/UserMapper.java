package com.teamup.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.teamup.main.dto.request.UserCreationRequest;
import com.teamup.main.dto.request.UserUpdateRequest;
import com.teamup.main.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "faculty", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    User toUser(UserCreationRequest request);
    
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}