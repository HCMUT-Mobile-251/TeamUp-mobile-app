package com.teamup.main.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamup.main.dto.request.SearchRequest;
import com.teamup.main.dto.response.GroupResponse;
import com.teamup.main.enums.GroupStatus;
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
    GroupService groupService;

    @Autowired
    GroupMapper groupMapper;

    @Autowired
    CourseService courseService;

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

        // filter theo tags
        if (request.getTagId() != null && !request.getTagId().isEmpty()) {
            groups = groups.stream()
                    .filter(g -> {
                        // Group phải có ít nhất 1 tag khớp với danh sách tag tìm kiếm
                        for (String tagId : request.getTagId()) {
                            if (g.getGroupTags().stream()
                                    .anyMatch(gt -> gt.getTag().getTagId().equalsIgnoreCase(tagId.trim()))) {
                                return true;
                            }
                        }
                        return false;
                    })
                    .toList();
        }

        // handle transform to GroupResponse and set isMember
        return groups.stream().map(g -> {
            GroupResponse response = groupMapper.toSearchGroup(g);
            response.setIsMember(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
                    request.getUserId(), g.getGroupId(), GroupStatus.JOINED));
            return response;
        }).toList();
    }

    // 1 ô tìm kiếm bình thường
    public List<GroupResponse> normalSearchGroup(String query, String userId) {
        // tránh trùng
        Set<Groups> results = new HashSet<>();
        results.addAll(groupRepository.findByTopicNameContainingIgnoreCase(query));
        results.addAll(groupRepository.findByNameContainingIgnoreCase(query));
        results.addAll(groupRepository.findByGroupClassContainingIgnoreCase(query));
        results.addAll(groupRepository.findByCourse_CourseIdOrCourse_NameContainingIgnoreCase(query, query));

        // Tìm kiếm theo tag name
        List<Groups> allGroups = groupRepository.findBySemester(groupService.getCurrentSemester());
        for (Groups g : allGroups) {
            // Kiểm tra xem group có tag nào khớp với query không
            boolean hasMatchingTag = g.getGroupTags().stream()
                    .anyMatch(gt -> gt.getTag().getName().toLowerCase().contains(query.toLowerCase()));
            if (hasMatchingTag) {
                results.add(g);
            }
        }

        // lấy semester hiện tại và map về response
        int currentSemester = groupService.getCurrentSemester();
        Set<GroupResponse> finalResults = results.stream()
                .filter(g -> g.getSemester() == currentSemester)
                .map(groupMapper::toSearchGroup)
                .collect(java.util.stream.Collectors.toSet());

        // set isMember
        return finalResults.stream()
                .map(response -> {
                    response.setIsMember(
                            groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(userId,
                                    response.getGroupId(), GroupStatus.JOINED));
                    return response;
                })
                .toList();
    }
}
