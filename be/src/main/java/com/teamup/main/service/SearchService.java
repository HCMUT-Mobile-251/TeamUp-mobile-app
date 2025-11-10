package com.teamup.main.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamup.main.dto.request.SearchRequest;
import com.teamup.main.dto.response.GroupResponse;
import com.teamup.main.mapper.GroupMapper;
import com.teamup.main.model.Courses;
import com.teamup.main.model.Groups;
import com.teamup.main.repository.GroupRepository;

import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class SearchService {
    @Autowired
    GroupRepository groupRepository;

    @Autowired
    GroupMapper groupMapper;

    @Autowired
    CourseService courseService;

    @Autowired
    GroupService groupService;

    // tìm nâng cao, lọc theo tầng
    public List<GroupResponse> advanceSearchGroups(SearchRequest request) {
        // chỉ lấy group ở học kỳ hiện tại
        List<Groups> groups = groupRepository.findBySemester(groupService.getCurrentSemester());

        if (request.getCourse() != null) {
            if ((request.getCourse().getCourseId() != null && !request.getCourse().getCourseId().trim().isBlank())) {
                Courses course = courseService.findCourse(request.getCourse().getCourseId()); // check course tồn tại
                groups = groups.stream()
                        .filter(g -> g.getCourse().getCourseId().equals(course.getCourseId()))
                        .toList();
            } else if (request.getCourse().getName() != null && !request.getCourse().getName().trim().isBlank()) {
                groups = groups.stream()
                        .filter(g -> g.getCourse().getName().toLowerCase()
                                .contains(request.getCourse().getName().trim().toLowerCase()))
                        .toList();
            }
        }

        if (request.getName() != null && !request.getName().trim().isBlank()) {
            groups = groups.stream()
                    .filter(g -> g.getName().toLowerCase().contains(request.getName().trim().toLowerCase()))
                    .toList();
        }

        if (request.getTopicName() != null && !request.getTopicName().trim().isBlank()) {
            groups = groups.stream()
                    .filter(g -> g.getTopicName().toLowerCase().contains(request.getTopicName().trim().toLowerCase()))
                    .toList();
        }

        if (request.getGroupClass() != null && !request.getGroupClass().trim().isBlank()) {
            groups = groups.stream()
                    .filter(g -> g.getGroupClass().toLowerCase().contains(request.getGroupClass().trim().toLowerCase()))
                    .toList();
        }

        if (request.getCourse() != null
                || request.getCourse().getCourseId().isBlank()
                || request.getCourse().getName().isBlank()
                || request.getName().isBlank()
                || request.getTopicName().isBlank()
                || request.getGroupClass().isBlank()) {
            groups = groups.stream()
                    .filter(g -> {
                        for (String tag : request.getTagId()) {
                            if (g.getGroupTags().stream()
                                    .anyMatch(t -> t.getTag().getTagId().equalsIgnoreCase(tag.trim()))) {
                                return true;
                            }
                        }
                        return false;
                    })
                    .toList();
        }

        return groups.stream().map(groupMapper::toSearchGroup).toList();
    }

    // 1 ô tìm kiếm bình thường
    public List<GroupResponse> normalSearchGroup(String group) {
        // tránh trùng
        Set<Groups> results = new HashSet<>();
        results.addAll(groupRepository.findByTopicNameContainingIgnoreCase(group));
        results.addAll(groupRepository.findByNameContainingIgnoreCase(group));
        results.addAll(groupRepository.findByGroupClassContainingIgnoreCase(group));
        results.addAll(groupRepository.findByCourse_CourseIdOrCourse_NameContainingIgnoreCase(group, group));

        // lấy semester hiện tại và map về response
        int currentSemester = groupService.getCurrentSemester();
        Set<GroupResponse> finalResults = results.stream()
                .filter(g -> g.getSemester() == currentSemester)
                .map(groupMapper::toSearchGroup)
                .collect(java.util.stream.Collectors.toSet());

        return finalResults.stream().toList();
    }
}
