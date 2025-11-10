package com.teamup.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.teamup.main.dto.request.GroupRequest;
import com.teamup.main.dto.response.GroupResponse;
import com.teamup.main.model.Groups;

@Mapper(componentModel = "spring")
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

    GroupResponse toSearchGroup(Groups group);
}
