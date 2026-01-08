package com.teamup.main.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.teamup.main.dto.request.GroupRequest;
import com.teamup.main.dto.response.GroupResponse;
import com.teamup.main.dto.response.UserResponse;
import com.teamup.main.exception.AppException;
import com.teamup.main.enums.ErrorCode;
import com.teamup.main.enums.GroupStatus;
import com.teamup.main.mapper.GroupMapper;
import com.teamup.main.mapper.UserMapper;
import com.teamup.main.model.Courses;
import com.teamup.main.model.Groups;
import com.teamup.main.model.GroupMember;
import com.teamup.main.model.GroupTag;
import com.teamup.main.model.PairId;
import com.teamup.main.model.Tags;
import com.teamup.main.model.Users;
import com.teamup.main.repository.GroupRepository;

import jakarta.transaction.Transactional;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class GroupService {
    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserService userService;

    @Autowired
    CourseService courseService;

    @Autowired
    TagService tagService;

    @Autowired
    GroupMapper groupMapper;

    @Autowired
    UserMapper userMapper;

    /*
     * User only
     */
    @Transactional
    public Groups createGroup(GroupRequest groupRequest) {
        // check user tồn tại
        Users leader = userService.findById(groupRequest.getLeaderId());
        // check course tồn tại
        Courses course = courseService.findCourse(groupRequest.getCourseId());

        // Tạo group và persist trước để có id
        Groups group = groupMapper.toCreateGroup(groupRequest);
        group.setCourse(course);
        group.setSemester(getCurrentSemester());
        // leader
        group.setLeaderId(leader);

        // để có groupId
        group = groupRepository.save(group);
        PairId id = new PairId(leader.getUserId(), group.getGroupId());
        Instant now = Instant.now();
        GroupMember leaderMember = new GroupMember(id, GroupStatus.JOINED, GroupStatus.CREATE_GROUP.getDescription(),
                now, false, leader, group);

        // thêm leader vào group members
        group.addMember(leaderMember);

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

        // check user phải thuộc group mới được chuyển thành leader
        Users leader = userService.findById(groupRequest.getLeaderId());
        for (UserResponse member : getMembers(groupRequest.getGroupId())) {
            if (member.getUserId().equals(leader.getUserId())) {
                groupMapper.toUpdateGroup(group, groupRequest);

                // phải set tay
                group.setLeaderId(leader);
                group.setCourse(courseService.findCourse(groupRequest.getCourseId()));
                return groupRepository.save(group);
            }
        }
        throw new AppException(ErrorCode.USER_NOT_IN_GROUP);
    }

    public void updateGroupTag(String groupId, List<Tags> listTag) {
        // check group tồn tại
        Groups group = findGroup(groupId);

        group.getGroupTags().clear();
        for (Tags tag : listTag) {
            // check tag tồn tại
            tag = tagService.findTag(tag.getTagId());
            PairId id = new PairId(group.getGroupId(), tag.getTagId());
            GroupTag groupTag = new GroupTag(id, group, tag);
            group.getGroupTags().add(groupTag);
        }
        groupRepository.save(group);
    }

    public List<UserResponse> getMembers(String groupId) {
        Groups group = findGroup(groupId);

        return group.getGroupMembers()
                .stream()
                .filter(gm -> gm.getStatus() == GroupStatus.JOINED)
                .map(gm -> {
                    Users u = gm.getUser();
                    return userMapper.queryUser(new UserResponse(), u);
                })
                .toList();
    }

    public Groups findGroup(String groupId) {
        return groupRepository.findById(groupId).orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));
    }

    public Boolean isJoinAnotherGroupWithSameCourse(String userId, String courseId) {
        int semester = getCurrentSemester();
        Groups groups = groupRepository
                .findGroupWithJoinedMember(semester,
                        courseId, userId);
        if (groups != null) {
            return true;
        }
        return false;
    }

    public int getSize(String groupId) {
        return (int) findGroup(groupId).getGroupMembers().stream()
                .filter(member -> member.getStatus() == GroupStatus.JOINED)
                .count();
    }

    public Groups addMember(String groupId, List<String> listUserId) {
        Groups group = findGroup(groupId);
        List<Users> users = userService.findAllById(listUserId);

        // check size list > group size
        if (getSize(groupId) + users.size() > (int) group.getMaxMembers()) {
            throw new AppException(ErrorCode.GROUP_FULL);
        }

        // ! check list sai 1 vài userid báo lỗi kịp thời, tạm chưa cần
        // if (users.size() != listUserId.size()) {
        // throw new AppException(ErrorCode.USER_NOT_FOUND);
        // }

        if (!users.isEmpty()) {
            for (Users user : users) {
                // đã trong nhóm
                if (groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(user.getUserId(),
                        groupId,
                        GroupStatus.JOINED)) {
                    throw new AppException(ErrorCode.USER_ALREADY_IN_GROUP);
                }

                // check user đã trong group khác với môn học tương tự trong kỳ này chưa
                if (isJoinAnotherGroupWithSameCourse(user.getUserId(), group.getCourse().getCourseId())) {
                    throw new AppException(ErrorCode.USER_JOINED_ANOTHER_GROUP_SAME_COURSE);
                }

                // tìm xem user đã từng gửi yêu cầu chưa
                // đang send request thì join luôn
                Optional<GroupMember> existing = group.getGroupMembers().stream()
                        .filter(member -> member.getUser().getUserId().equals(user.getUserId())
                                && member.getStatus() == GroupStatus.WAITING_APPROVAL)
                        .findFirst();
                if (existing.isPresent()) {
                    GroupMember member = existing.get();
                    group.addMember(member);
                    member.setStatus(GroupStatus.JOINED);
                    return groupRepository.save(group); // return sớm
                }

                // filter theo PairId, tồn tại thì xóa cái cũ
                group.getGroupMembers().stream()
                        .filter(member -> member.getUser().getUserId().equals(user.getUserId())
                                && (member.getStatus() != GroupStatus.JOINED))
                        .findFirst()
                        .ifPresent(member -> group.removeMember(member));

                PairId id = new PairId(user.getUserId(), group.getGroupId());
                Instant now = Instant.now();
                // thêm vào group với trạng thái chờ đối phương phê duyệt
                GroupMember groupMember = new GroupMember(id, GroupStatus.PENDING_APPROVAL,
                        GroupStatus.ADD_MEMBER.getDescription(), now, false, user,
                        group);
                group.addMember(groupMember);
            }
            return groupRepository.save(group);
        }
        throw new AppException(ErrorCode.USER_NOT_FOUND);
    }

    public Groups inviteMemberByIdentifier(String groupId, String identifier) {
        Groups group = findGroup(groupId);

        // check group đã đầy chưa
        if (getSize(groupId) >= (int) group.getMaxMembers()) {
            throw new AppException(ErrorCode.GROUP_FULL);
        }

        // Tìm user theo MSSV hoặc Email
        Users foundUser = null;

        // Kiểm tra xem identifier có phải là email không (chứa @)
        if (identifier.contains("@")) {
            // Tìm theo email
            foundUser = userService.findByEmail(identifier);
        } else {
            // Tìm theo studentId
            List<Users> usersByStudentId = userService.findByStudentId(identifier);
            if (!usersByStudentId.isEmpty()) {
                foundUser = usersByStudentId.get(0);
            }
        }

        if (foundUser == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        final Users user = foundUser;

        // Kiểm tra user đã trong nhóm chưa (check trong collection hiện tại)
        boolean isAlreadyJoined = group.getGroupMembers().stream()
                .anyMatch(member -> member.getUser().getUserId().equals(user.getUserId())
                        && member.getStatus() == GroupStatus.JOINED);

        if (isAlreadyJoined) {
            throw new AppException(ErrorCode.USER_ALREADY_IN_GROUP);
        }

        // check user đã trong group khác với môn học tương tự trong kỳ này chưa
        if (isJoinAnotherGroupWithSameCourse(user.getUserId(), group.getCourse().getCourseId())) {
            throw new AppException(ErrorCode.USER_JOINED_ANOTHER_GROUP_SAME_COURSE);
        }

        // tìm xem user đã từng gửi yêu cầu chưa
        // đang send request thì join luôn
        Optional<GroupMember> existing = group.getGroupMembers().stream()
                .filter(member -> member.getUser().getUserId().equals(user.getUserId())
                        && member.getStatus() == GroupStatus.WAITING_APPROVAL)
                .findFirst();
        if (existing.isPresent()) {
            GroupMember member = existing.get();
            group.addMember(member);
            member.setStatus(GroupStatus.JOINED);
            return groupRepository.save(group);
        }

        // filter theo PairId, tồn tại thì xóa cái cũ
        group.getGroupMembers().stream()
                .filter(member -> member.getUser().getUserId().equals(user.getUserId())
                        && (member.getStatus() != GroupStatus.JOINED))
                .findFirst()
                .ifPresent(member -> group.removeMember(member));

        PairId id = new PairId(user.getUserId(), group.getGroupId());
        Instant now = Instant.now();
        // thêm vào group với trạng thái chờ đối phương phê duyệt
        GroupMember groupMember = new GroupMember(id, GroupStatus.PENDING_APPROVAL,
                GroupStatus.ADD_MEMBER.getDescription(), now, false, user, group);
        group.addMember(groupMember);

        return groupRepository.save(group);
    }

    public void joinRequest(String groupId, String userId, String message) {
        Groups group = findGroup(groupId);
        Users user = userService.findById(userId);

        // check đủ members chưa
        if (getSize(groupId) >= (int) group.getMaxMembers()) {
            throw new AppException(ErrorCode.GROUP_FULL);
        }

        // tìm xem user đã từng gửi yêu cầu chưa
        // được mời thì join luôn
        Optional<GroupMember> existing = group.getGroupMembers().stream()
                .filter(member -> member.getUser().getUserId().equals(user.getUserId())
                        && member.getStatus() == GroupStatus.PENDING_APPROVAL)
                .findFirst();
        if (existing.isPresent()) {
            GroupMember member = existing.get();
            group.addMember(member);
            member.setStatus(GroupStatus.JOINED);
            return; // return sớm
        }

        // filter theo PairId, tồn tại thì xóa cái cũ
        group.getGroupMembers().stream()
                .filter(member -> member.getUser().getUserId().equals(userId)
                        && (member.getStatus() != GroupStatus.JOINED))
                .findFirst()
                .ifPresent(member -> group.removeMember(member));

        // đã trong nhóm
        if (groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(userId, groupId,
                GroupStatus.JOINED)) {
            throw new AppException(ErrorCode.USER_ALREADY_IN_GROUP);
        }

        // check user đã trong group khác với môn học tương tự trong kỳ này chưa
        if (isJoinAnotherGroupWithSameCourse(userId, group.getCourse().getCourseId())) {
            throw new AppException(ErrorCode.USER_JOINED_ANOTHER_GROUP_SAME_COURSE);
        }

        PairId id = new PairId(userId, groupId);
        Instant now = Instant.now();
        GroupMember groupMember = new GroupMember(id, GroupStatus.WAITING_APPROVAL, message, now, false, user, group);
        group.addMember(groupMember);
        groupRepository.save(group);
    }

    // dùng cho leader và user từ chối lời mời
    public void rejectJoinRequest(String groupId, String userId) {
        Groups group = findGroup(groupId);

        try {
            group.getGroupMembers().stream()
                    .filter(member -> member.getUser().getUserId().equals(userId)
                            && (member.getStatus() == GroupStatus.WAITING_APPROVAL
                                    || member.getStatus() == GroupStatus.PENDING_APPROVAL))
                    .findFirst()
                    .ifPresent(member -> member.setStatus(GroupStatus.REJECTED));
        } catch (Exception e) {
            throw new AppException(ErrorCode.USER_NOT_IN_GROUP);
        }
        groupRepository.save(group);
    }

    // dùng cho leader và user chấp nhận lời mời
    public void acceptJoinRequest(String groupId, String userId) {
        Groups group = findGroup(groupId);

        // check user đã trong group khác với môn học tương tự trong kỳ này chưa
        if (isJoinAnotherGroupWithSameCourse(userId, group.getCourse().getCourseId())) {
            throw new AppException(ErrorCode.USER_JOINED_ANOTHER_GROUP_SAME_COURSE);
        }

        try {
            group.getGroupMembers().stream()
                    .filter(member -> member.getUser().getUserId().equals(userId)
                            && (member.getStatus() == GroupStatus.WAITING_APPROVAL
                                    || member.getStatus() == GroupStatus.PENDING_APPROVAL))
                    .findFirst()
                    .ifPresent(member -> member.setStatus(GroupStatus.JOINED));
        } catch (Exception e) {
            throw new AppException(ErrorCode.USER_NOT_IN_GROUP);
        }
        groupRepository.save(group);
    }

    public Groups kickOrOutGroup(String groupId, String userId, Boolean isKick) {
        Groups group = findGroup(groupId);

        // check user có phải leader
        if (group.getLeaderId().getUserId().equals(userId)) {
            // Đếm số thành viên đã được accept (JOINED)
            long acceptedMemberCount = group.getGroupMembers().stream()
                    .filter(member -> member.getStatus() == GroupStatus.JOINED)
                    .count();

            System.out.println("Leader rời nhóm - Số thành viên JOINED: " + acceptedMemberCount);
            System.out.println("Tổng số groupMembers: " + group.getGroupMembers().size());

            // Nếu leader là người duy nhất (không có thành viên nào khác), xóa nhóm
            if (acceptedMemberCount == 0) {
                System.out.println("Xóa nhóm " + groupId + " vì leader là thành viên duy nhất");
                deleteGroup(groupId);
                return group;
            } else {
                // Nếu có thành viên khác, yêu cầu chuyển quyền leader trước
                System.out.println("Không thể xóa nhóm - còn " + acceptedMemberCount + " thành viên JOINED");
                throw new AppException(ErrorCode.NO_LEADER);
            }
        }

        // check user có trong group
        try {
            group.getGroupMembers().stream()
                    .filter(member -> member.getUser().getUserId().equals(userId)
                            && (member.getStatus() == GroupStatus.JOINED
                                    || member.getStatus() == GroupStatus.WAITING_APPROVAL))
                    .findFirst()
                    .ifPresent(member -> member.setStatus(isKick ? GroupStatus.REMOVED : GroupStatus.LEFT));
        } catch (Exception e) {
            throw new AppException(ErrorCode.USER_NOT_IN_GROUP);
        }
        return groupRepository.save(group);
    }

    public void deleteGroup(String groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new AppException(ErrorCode.GROUP_NOT_FOUND);
        }
        groupRepository.deleteById(groupId);
    }

    public List<GroupResponse> getIndividualGroups(int pageNumber, int pageSize, String userId) {
        int semester = getCurrentSemester();
        List<String> tagIds = userService.findById(userId).getUserTags()
                .stream()
                .map(t -> t.getTag().getTagId())
                .toList();

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        // nếu user không có tag nào thì trả về bất kỳ
        if (tagIds.isEmpty()) {
            Page<Groups> page = groupRepository.findBySemester(semester, pageable);
            List<Groups> groups = page.getContent();
            return groups.stream().map(g -> {
                GroupResponse response = groupMapper.toSearchGroup(g);
                response.setIsMember(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
                        userId, g.getGroupId(), GroupStatus.JOINED));
                return response;
            }).toList();
        }

        Page<Groups> page = groupRepository.findBySemesterAndGroupTags_Id_SecondIdIn(
                semester,
                tagIds,
                pageable);
        List<Groups> groups = page.getContent();

        return groups.stream().map(g -> {
            GroupResponse response = groupMapper.toSearchGroup(g);
            response.setIsMember(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
                    userId, g.getGroupId(), GroupStatus.JOINED));
            return response;
        }).toList();
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
}
