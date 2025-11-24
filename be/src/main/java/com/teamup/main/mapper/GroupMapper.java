package com.teamup.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.teamup.main.dto.request.GroupRequest;
import com.teamup.main.dto.response.GroupResponse;
import com.teamup.main.model.Groups;

// ignore các trường không có trong request, giữ nguyên giá trị cũ bên model
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GroupMapper {
    @Mapping(target = "semester", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "leaderId", ignore = true)
    @Mapping(target = "groupMembers", ignore = true)
    @Mapping(target = "groupTags", ignore = true)
    @Mapping(target = "groupId", expression = "java(null)") // để JPA generate id
    Groups toCreateGroup(GroupRequest request);

    @Mapping(target = "semester", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "leaderId", ignore = true)
    @Mapping(target = "groupMembers", ignore = true)
    @Mapping(target = "groupTags", ignore = true)
    void toUpdateGroup(@MappingTarget Groups group, GroupRequest request);

    @Mapping(target = "isMember", ignore = true)
    GroupResponse toSearchGroup(Groups group);
}
