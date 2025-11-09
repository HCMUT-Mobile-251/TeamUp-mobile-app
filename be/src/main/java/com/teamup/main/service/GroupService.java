package com.teamup.main.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.teamup.main.dto.request.GroupRequest;
import com.teamup.main.dto.response.UserResponse;
import com.teamup.main.exception.AppException;
import com.teamup.main.exception.ErrorCode;
import com.teamup.main.mapper.GroupMapper;
import com.teamup.main.mapper.UserMapper;
import com.teamup.main.model.Courses;
import com.teamup.main.model.Groups;
import com.teamup.main.model.GroupMember;
import com.teamup.main.model.PairId;
import com.teamup.main.model.Users;
import com.teamup.main.repository.CourseRepository;
import com.teamup.main.repository.GroupRepository;
import com.teamup.main.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class GroupService {
    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    GroupMapper groupMapper;

    @Autowired
    UserMapper userMapper;

    /*
     * User only
     */
    @Transactional
    public Groups createGroup(GroupRequest groupRequest) {
        Users user = userRepository.findById(groupRequest.getLeaderId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Courses course = courseRepository.findById(groupRequest.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // Tạo group và persist trước để có id
        Groups group = groupMapper.toCreateGroup(groupRequest);
        group.setCourse(course);
        group.setSemester(getCurrentSemester());
        group.setLeaderId(user); // leader

        // để có groupId
        System.out.println("dsd" + user.getUserId());
        group = groupRepository.save(group);
        System.out.println("23232" + user.getUserId() + " " + group.getGroupId());
        PairId id = new PairId(user.getUserId(), group.getGroupId());
        GroupMember leaderMember = new GroupMember();
        leaderMember.setId(id);
        leaderMember.setUser(user);
        leaderMember.setGroup(group);
        leaderMember.setJoinMessage("It's mine!");

        // Add vào collection (cascade sẽ persist)
        group.getGroupMembers().add(leaderMember);

        return groupRepository.save(group);
    }

    public int getCurrentSemester() {
        LocalDate today = LocalDate.now();
        int year = Integer.parseInt(String.valueOf(today.getYear()).substring(1));
        int semester = (today.getMonthValue() > 4) ? ((today.getMonthValue() > 8) ? 1 : 3) : 2;

        return year * 10 + semester;
    }

    public Groups updateGroup(GroupRequest groupRequest) {
        Groups group = findGroup(groupRequest.getGroupId());

        // check user phải thuộc group mới được update
        Users leader = userRepository.findById(groupRequest.getLeaderId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        for (UserResponse member : getMembers(groupRequest.getGroupId())) {
            if (member.getUserId().equals(leader.getUserId())) {
                groupMapper.toUpdateGroup(group, groupRequest);
                group.setLeaderId(leader);
                group.setCourse(courseRepository.findById(groupRequest.getCourseId())
                        .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND)));
                return groupRepository.save(group);
            }
        }
        throw new AppException(ErrorCode.USER_NOT_IN_GROUP);

    }

    public List<UserResponse> getMembers(String groupId) {
        Groups group = findGroup(groupId);
        return group.getGroupMembers()
                .stream()
                .map(gm -> {
                    Users u = gm.getUser();
                    return userMapper.queryUser(new UserResponse(), u);
                })
                .toList();
    }

    public Groups findGroup(String groupId) {
        return groupRepository.findById(groupId).orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));
    }

    public Groups addMember(String groupId, List<String> listUserId) {
        Groups group = findGroup(groupId);
        List<Users> users = userRepository.findAllById(listUserId);

        // kiểm tra size list > group size
        if (group.getGroupMembers().size() + users.size() > (int) group.getMaxMembers()) {
            throw new AppException(ErrorCode.GROUP_FULL);
        }

        // ! kiểm tra list sai 1 vài userid báo lỗi kịp thời, tạm chưa cần
        // if (users.size() != listUserId.size()) {
        // throw new AppException(ErrorCode.USER_NOT_FOUND);
        // }

        if (!users.isEmpty()) {
            for (Users user : users) {
                GroupMember groupMember = new GroupMember();
                PairId id = new PairId(user.getUserId(), group.getGroupId());
                groupMember.setId(id);
                groupMember.setUser(user);
                groupMember.setGroup(group);
                groupMember.setJoinMessage("Add by leader!");
                group.getGroupMembers().add(groupMember);
            }
            return groupRepository.save(group);
        }
        throw new AppException(ErrorCode.USER_NOT_FOUND);
    }

    public Groups kickOrOutGroup(String groupId, String userId) {
        Groups group = findGroup(groupId);

        // check user có phải leader
        if (group.getLeaderId().getUserId().equals(userId)) {
            throw new AppException(ErrorCode.NO_LEADER);
        }

        // check user có trong group
        for (UserResponse user : getMembers(groupId)) {
            if (user.getUserId().equals(userId)) {
                if (!group.getGroupMembers()
                        .removeIf(member -> member.getUser().getUserId().equals(userId))) {
                    throw new AppException(ErrorCode.USER_NOT_IN_GROUP);
                }
                return groupRepository.save(group);
            }
        }
        throw new AppException(ErrorCode.USER_NOT_IN_GROUP);
    }

    public void deleteGroup(String groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new AppException(ErrorCode.GROUP_NOT_FOUND);
        }
        groupRepository.deleteById(groupId);
    }

    /*
     * Admin only
     */
    public List<Groups> getGroups(int pageNumber, int pageSize) { // (index page, size page)
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Groups> page = groupRepository.findAll(pageable);
        List<Groups> groups = page.getContent();

        // int totalPages = page.getTotalPages();
        // long totalItems = page.getTotalElements();
        return groups;
    }

    public List<Groups> getSearchNormalGroup(String group) {
        List<Groups> byId = groupRepository.findByGroupClassContainingIgnoreCase(group);
        if (!byId.isEmpty()) {
            return byId;
        }
        List<Groups> byName = groupRepository.findByNameContainingIgnoreCase(group);
        return byName;
    }
}
