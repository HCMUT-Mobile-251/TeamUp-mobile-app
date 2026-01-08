package com.teamup.main.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.teamup.main.dto.request.GroupRequest;
import com.teamup.main.dto.response.GroupResponse;
import com.teamup.main.enums.ErrorCode;
import com.teamup.main.enums.GroupStatus;
import com.teamup.main.exception.AppException;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("GroupService Advanced Coverage Tests - Phase 8")
class GroupServiceAdvancedTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserService userService;

    @Mock
    private CourseService courseService;

    @Mock
    private TagService tagService;

    @Mock
    private GroupMapper groupMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private GroupService groupService;

    private Groups testGroup;
    private Users leaderUser;
    private Users memberUser;
    private Courses testCourse;
    private GroupRequest groupRequest;

    @BeforeEach
    void setUp() {
        // Setup leader user
        leaderUser = new Users();
        leaderUser.setUserId("leader123");
        leaderUser.setFirstName("Leader");
        leaderUser.setLastName("User");
        leaderUser.setEmail("leader@test.com");
        leaderUser.setStudentId("20000001");

        // Setup member user
        memberUser = new Users();
        memberUser.setUserId("member123");
        memberUser.setFirstName("Member");
        memberUser.setLastName("User");
        memberUser.setEmail("member@test.com");
        memberUser.setStudentId("20000002");

        // Setup course
        testCourse = new Courses();
        testCourse.setCourseId("CS101");
        testCourse.setName("Introduction to Computer Science");

        // Setup group
        testGroup = new Groups();
        testGroup.setGroupId("group123");
        testGroup.setName("Test Group");
        testGroup.setDescription("Test Description");
        testGroup.setSemester(20242);
        testGroup.setMaxMembers(5);
        testGroup.setLeaderId(leaderUser);
        testGroup.setCourse(testCourse);

        // Setup group request
        groupRequest = new GroupRequest();
        groupRequest.setGroupId("group123");
        groupRequest.setName("New Group");
        groupRequest.setTopicName("New Topic");
        groupRequest.setLeaderId("leader123");
        groupRequest.setCourseId("CS101");
    }

    // ===== CREATE GROUP TESTS =====
    @Test
    @DisplayName("Should create group successfully with valid request")
    void testCreateGroupSuccess() {
        when(userService.findById("leader123")).thenReturn(leaderUser);
        when(courseService.findCourse("CS101")).thenReturn(testCourse);
        when(groupMapper.toCreateGroup(groupRequest)).thenReturn(testGroup);
        when(groupRepository.save(any(Groups.class))).thenReturn(testGroup);

        Groups result = groupService.createGroup(groupRequest);

        assertNotNull(result);
        assertEquals("group123", result.getGroupId());
        verify(userService).findById("leader123");
        verify(courseService).findCourse("CS101");
        verify(groupRepository, times(2)).save(any(Groups.class));
    }

    @Test
    @DisplayName("Should set leader when creating group")
    void testCreateGroupSetsLeader() {
        when(userService.findById("leader123")).thenReturn(leaderUser);
        when(courseService.findCourse("CS101")).thenReturn(testCourse);
        when(groupMapper.toCreateGroup(groupRequest)).thenReturn(testGroup);
        when(groupRepository.save(any(Groups.class))).thenReturn(testGroup);

        Groups result = groupService.createGroup(groupRequest);

        assertNotNull(result);
        assertEquals(leaderUser, result.getLeaderId());
    }

    @Test
    @DisplayName("Should throw exception when leader user not found")
    void testCreateGroupLeaderNotFound() {
        when(userService.findById("invalid")).thenThrow(new AppException(ErrorCode.USER_NOT_FOUND));
        groupRequest.setLeaderId("invalid");

        assertThrows(AppException.class, () -> groupService.createGroup(groupRequest));
        verify(userService).findById("invalid");
    }

    @Test
    @DisplayName("Should throw exception when course not found")
    void testCreateGroupCourseNotFound() {
        when(userService.findById("leader123")).thenReturn(leaderUser);
        when(courseService.findCourse("INVALID")).thenThrow(new AppException(ErrorCode.COURSE_NOT_FOUND));
        groupRequest.setCourseId("INVALID");

        assertThrows(AppException.class, () -> groupService.createGroup(groupRequest));
        verify(userService).findById("leader123");
        verify(courseService).findCourse("INVALID");
    }

    // ===== UPDATE GROUP TESTS =====
    @Test
    @DisplayName("Should update group successfully")
    void testUpdateGroupSuccess() {
        // Add leader as member
        PairId pairId = new PairId("leader123", "group123");
        GroupMember leaderMember = new GroupMember(pairId, GroupStatus.JOINED, "Leader", Instant.now(), false, leaderUser, testGroup);
        testGroup.addMember(leaderMember);

        // Setup mock for UserResponse
        com.teamup.main.dto.response.UserResponse leaderResponse = new com.teamup.main.dto.response.UserResponse();
        leaderResponse.setUserId("leader123");
        leaderResponse.setFirstName("Leader");

        when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
        when(userService.findById("leader123")).thenReturn(leaderUser);
        when(courseService.findCourse("CS101")).thenReturn(testCourse);
        doNothing().when(groupMapper).toUpdateGroup(testGroup, groupRequest);
        when(groupRepository.save(testGroup)).thenReturn(testGroup);
        when(userMapper.queryUser(any(), any())).thenReturn(leaderResponse);

        Groups result = groupService.updateGroup(groupRequest);

        assertNotNull(result);
        verify(groupRepository).save(testGroup);
    }

    @Test
    @DisplayName("Should throw exception when group not found for update")
    void testUpdateGroupNotFound() {
        groupRequest.setGroupId("invalid");
        when(groupRepository.findById("invalid")).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> groupService.updateGroup(groupRequest));
        verify(groupRepository).findById("invalid");
    }

    @Test
    @DisplayName("Should throw exception when leader not in group")
    void testUpdateGroupLeaderNotInGroup() {
        when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
        when(userService.findById("leader123")).thenReturn(leaderUser);

        // This will result in empty member list, causing USER_NOT_IN_GROUP error
        groupRequest.setLeaderId("leader123");

        assertThrows(AppException.class, () -> groupService.updateGroup(groupRequest));
    }

    // ===== DELETE GROUP TESTS =====
    @Test
    @DisplayName("Should delete group successfully")
    void testDeleteGroupSuccess() {
        when(groupRepository.existsById("group123")).thenReturn(true);
        doNothing().when(groupRepository).deleteById("group123");

        groupService.deleteGroup("group123");

        verify(groupRepository).existsById("group123");
        verify(groupRepository).deleteById("group123");
    }

    @Test
    @DisplayName("Should throw exception when group not found for delete")
    void testDeleteGroupNotFound() {
        when(groupRepository.existsById("invalid")).thenReturn(false);

        assertThrows(AppException.class, () -> groupService.deleteGroup("invalid"));
        verify(groupRepository).existsById("invalid");
        verify(groupRepository, never()).deleteById(any());
    }

    // ===== FIND GROUP TESTS =====
    @Test
    @DisplayName("Should find group by id successfully")
    void testFindGroupSuccess() {
        when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));

        Groups result = groupService.findGroup("group123");

        assertNotNull(result);
        assertEquals("group123", result.getGroupId());
        verify(groupRepository).findById("group123");
    }

    @Test
    @DisplayName("Should throw exception when finding non-existent group")
    void testFindGroupNotFound() {
        when(groupRepository.findById("invalid")).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> groupService.findGroup("invalid"));
        verify(groupRepository).findById("invalid");
    }

    @Test
    @DisplayName("Should return group with all properties")
    void testFindGroupProperties() {
        when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));

        Groups result = groupService.findGroup("group123");

        assertEquals("group123", result.getGroupId());
        assertEquals("Test Group", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertEquals(5, result.getMaxMembers());
        assertEquals(testCourse.getCourseId(), result.getCourse().getCourseId());
    }

    // ===== ADD MEMBER TESTS =====
    @Test
    @DisplayName("Should add member to group successfully")
    void testAddMemberSuccess() {
        List<String> userIds = List.of("member123");
        List<Users> users = List.of(memberUser);

        when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
        when(userService.findAllById(userIds)).thenReturn(users);
        when(groupRepository.save(any(Groups.class))).thenReturn(testGroup);
        when(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
            "member123", "group123", GroupStatus.JOINED)).thenReturn(false);

        Groups result = groupService.addMember("group123", userIds);

        assertNotNull(result);
        verify(userService).findAllById(userIds);
        verify(groupRepository).save(any(Groups.class));
    }

    @Test
    @DisplayName("Should throw exception when user already in group")
    void testAddMemberAlreadyExists() {
        List<String> userIds = List.of("member123");
        List<Users> users = List.of(memberUser);

        when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
        when(userService.findAllById(userIds)).thenReturn(users);
        when(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
            "member123", "group123", GroupStatus.JOINED)).thenReturn(true);

        assertThrows(AppException.class, () -> groupService.addMember("group123", userIds));
    }

    @Test
    @DisplayName("Should throw exception when group is full")
    void testAddMemberGroupFull() {
        testGroup.setMaxMembers(1);
        
        // Add one member to fill the group
        PairId pairId = new PairId("member123", "group123");
        GroupMember member = new GroupMember(pairId, GroupStatus.JOINED, "Member", Instant.now(), false, memberUser, testGroup);
        testGroup.addMember(member);

        List<String> userIds = List.of("member123");
        List<Users> users = List.of(memberUser);

        when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
        when(userService.findAllById(userIds)).thenReturn(users);

        assertThrows(AppException.class, () -> groupService.addMember("group123", userIds));
    }

    // ===== SIZE TESTS =====
    @Test
    @DisplayName("Should calculate group size correctly")
    void testGetGroupSize() {
        PairId pairId1 = new PairId("user1", "group123");
        GroupMember member1 = new GroupMember(pairId1, GroupStatus.JOINED, "Member", Instant.now(), false, memberUser, testGroup);
        testGroup.addMember(member1);

        PairId pairId2 = new PairId("user2", "group123");
        Users user2 = new Users();
        user2.setUserId("user2");
        GroupMember member2 = new GroupMember(pairId2, GroupStatus.JOINED, "Member", Instant.now(), false, user2, testGroup);
        testGroup.addMember(member2);

        when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));

        int size = groupService.getSize("group123");

        assertEquals(2, size);
    }

    @Test
    @DisplayName("Should not count non-joined members in size")
    void testGetGroupSizeIgnoresNonJoined() {
        PairId pairId1 = new PairId("user1", "group123");
        GroupMember member1 = new GroupMember(pairId1, GroupStatus.JOINED, "Member", Instant.now(), false, memberUser, testGroup);
        testGroup.addMember(member1);

        PairId pairId2 = new PairId("user2", "group123");
        Users user2 = new Users();
        user2.setUserId("user2");
        GroupMember member2 = new GroupMember(pairId2, GroupStatus.WAITING_APPROVAL, "Pending", Instant.now(), false, user2, testGroup);
        testGroup.addMember(member2);

        when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));

        int size = groupService.getSize("group123");

        assertEquals(1, size); // Only JOINED members
    }

    // ===== UPDATE GROUP TAGS TESTS =====
    @Test
    @DisplayName("Should update group tags successfully")
    void testUpdateGroupTagsSuccess() {
        Tags tag = new Tags();
        tag.setTagId("tag1");
        tag.setTagName("Java");

        when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
        when(tagService.findTag("tag1")).thenReturn(tag);
        when(groupRepository.save(any(Groups.class))).thenReturn(testGroup);

        groupService.updateGroupTag("group123", List.of(tag));

        verify(groupRepository).findById("group123");
        verify(tagService).findTag("tag1");
        verify(groupRepository).save(testGroup);
    }

    @Test
    @DisplayName("Should clear existing tags when updating")
    void testUpdateGroupTagsClearsExisting() {
        Tags oldTag = new Tags();
        oldTag.setTagId("oldTag");
        oldTag.setTagName("Python");

        Tags newTag = new Tags();
        newTag.setTagId("tag1");
        newTag.setTagName("Java");

        PairId pairId = new PairId("group123", "oldTag");
        GroupTag existingTag = new GroupTag(pairId, testGroup, oldTag);
        testGroup.addTag(existingTag);

        when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
        when(tagService.findTag("tag1")).thenReturn(newTag);
        when(groupRepository.save(any(Groups.class))).thenReturn(testGroup);

        groupService.updateGroupTag("group123", List.of(newTag));

        assertTrue(testGroup.getGroupTags().stream()
            .anyMatch(gt -> gt.getTag().getTagId().equals("tag1")));
        verify(groupRepository).save(testGroup);
    }

    // ===== GET MEMBERS TESTS =====
    @Test
    @DisplayName("Should return only joined members")
    void testGetMembersReturnsOnlyJoined() {
        PairId pairId1 = new PairId("user1", "group123");
        GroupMember joinedMember = new GroupMember(pairId1, GroupStatus.JOINED, "Member", Instant.now(), false, memberUser, testGroup);
        testGroup.addMember(joinedMember);

        PairId pairId2 = new PairId("user2", "group123");
        Users user2 = new Users();
        user2.setUserId("user2");
        GroupMember pendingMember = new GroupMember(pairId2, GroupStatus.PENDING_APPROVAL, "Pending", Instant.now(), false, user2, testGroup);
        testGroup.addMember(pendingMember);

        when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
        when(userMapper.queryUser(any(), eq(memberUser))).thenReturn(new com.teamup.main.dto.response.UserResponse());

        List<com.teamup.main.dto.response.UserResponse> members = groupService.getMembers("group123");

        assertEquals(1, members.size());
        verify(groupRepository).findById("group123");
    }

    // ===== INTEGRATION TESTS =====
    @Test
    @DisplayName("Should handle full group lifecycle: create, find, update, delete")
    void testFullGroupLifecycle() {
        // Setup for create
        when(userService.findById("leader123")).thenReturn(leaderUser);
        when(courseService.findCourse("CS101")).thenReturn(testCourse);
        when(groupMapper.toCreateGroup(groupRequest)).thenReturn(testGroup);
        when(groupRepository.save(any(Groups.class))).thenReturn(testGroup);

        // Create
        Groups created = groupService.createGroup(groupRequest);
        assertNotNull(created);

        // Find
        when(groupRepository.findById("group123")).thenReturn(Optional.of(created));
        Groups found = groupService.findGroup("group123");
        assertNotNull(found);

        // Update
        PairId pairId = new PairId("leader123", "group123");
        GroupMember leaderMember = new GroupMember(pairId, GroupStatus.JOINED, "Leader", Instant.now(), false, leaderUser, found);
        found.addMember(leaderMember);

        com.teamup.main.dto.response.UserResponse leaderResponse = new com.teamup.main.dto.response.UserResponse();
        leaderResponse.setUserId("leader123");
        leaderResponse.setFirstName("Leader");

        when(userService.findById("leader123")).thenReturn(leaderUser);
        when(courseService.findCourse("CS101")).thenReturn(testCourse);
        doNothing().when(groupMapper).toUpdateGroup(found, groupRequest);
        when(groupRepository.save(found)).thenReturn(found);
        when(userMapper.queryUser(any(), any())).thenReturn(leaderResponse);

        Groups updated = groupService.updateGroup(groupRequest);
        assertNotNull(updated);

        // Delete
        when(groupRepository.existsById("group123")).thenReturn(true);
        doNothing().when(groupRepository).deleteById("group123");
        groupService.deleteGroup("group123");

        verify(groupRepository).deleteById("group123");
    }

    // ===== JOIN/REJECT/ACCEPT REQUEST TESTS =====
    @Test
    @DisplayName("Should handle join request successfully")
    void testJoinRequestSuccess() {
        when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
        when(userService.findById("member123")).thenReturn(memberUser);
        when(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
            "member123", "group123", GroupStatus.JOINED)).thenReturn(false);
        when(groupRepository.save(any(Groups.class))).thenReturn(testGroup);

        groupService.joinRequest("group123", "member123", "I want to join");

        verify(userService).findById("member123");
        verify(groupRepository).save(testGroup);
    }

    @Test
    @DisplayName("Should throw exception when group is full for join request")
    void testJoinRequestGroupFull() {
        testGroup.setMaxMembers(1);
        PairId pairId = new PairId("user1", "group123");
        GroupMember member = new GroupMember(pairId, GroupStatus.JOINED, "Member", Instant.now(), false, memberUser, testGroup);
        testGroup.addMember(member);

        when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));

        assertThrows(AppException.class, () -> groupService.joinRequest("group123", "member123", "Request"));
    }

    @Test
    @DisplayName("Should accept join request successfully")
    void testAcceptJoinRequestSuccess() {
        PairId pairId = new PairId("member123", "group123");
        GroupMember member = new GroupMember(pairId, GroupStatus.WAITING_APPROVAL, "Pending", Instant.now(), false, memberUser, testGroup);
        testGroup.addMember(member);

        when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
        when(groupRepository.save(any(Groups.class))).thenReturn(testGroup);

        groupService.acceptJoinRequest("group123", "member123");

        assertEquals(GroupStatus.JOINED, member.getStatus());
        verify(groupRepository).save(testGroup);
    }

    @Test
    @DisplayName("Should reject join request successfully")
    void testRejectJoinRequestSuccess() {
        PairId pairId = new PairId("member123", "group123");
        GroupMember member = new GroupMember(pairId, GroupStatus.WAITING_APPROVAL, "Pending", Instant.now(), false, memberUser, testGroup);
        testGroup.addMember(member);

        when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
        when(groupRepository.save(any(Groups.class))).thenReturn(testGroup);

        groupService.rejectJoinRequest("group123", "member123");

        assertEquals(GroupStatus.REJECTED, member.getStatus());
        verify(groupRepository).save(testGroup);
    }

    // ===== KICK/OUT GROUP TESTS =====
    @Test
    @DisplayName("Should kick member from group successfully")
    void testKickMemberSuccess() {
        PairId leaderPairId = new PairId("leader123", "group123");
        GroupMember leaderMember = new GroupMember(leaderPairId, GroupStatus.JOINED, "Leader", Instant.now(), false, leaderUser, testGroup);
        testGroup.addMember(leaderMember);

        PairId memberPairId = new PairId("member123", "group123");
        GroupMember memberToKick = new GroupMember(memberPairId, GroupStatus.JOINED, "Member", Instant.now(), false, memberUser, testGroup);
        testGroup.addMember(memberToKick);

        when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
        when(groupRepository.save(any(Groups.class))).thenReturn(testGroup);

        Groups result = groupService.kickOrOutGroup("group123", "member123", true);

        assertEquals(GroupStatus.REMOVED, memberToKick.getStatus());
        verify(groupRepository).save(testGroup);
    }

    @Test
    @DisplayName("Should throw exception when trying to kick leader")
    void testKickLeaderFails() {
        PairId leaderPairId = new PairId("leader123", "group123");
        GroupMember leaderMember = new GroupMember(leaderPairId, GroupStatus.JOINED, "Leader", Instant.now(), false, leaderUser, testGroup);
        testGroup.addMember(leaderMember);

        when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));

        assertThrows(AppException.class, () -> groupService.kickOrOutGroup("group123", "leader123", true));
    }

    // ===== ERROR HANDLING TESTS =====
    @Test
    @DisplayName("Should handle empty user list in addMember")
    void testAddMemberEmptyList() {
        when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
        when(userService.findAllById(List.of())).thenReturn(List.of());

        assertThrows(AppException.class, () -> groupService.addMember("group123", List.of()));
    }

    @Test
    @DisplayName("Should verify group properties after creation")
    void testGroupPropertiesAfterCreation() {
        when(userService.findById("leader123")).thenReturn(leaderUser);
        when(courseService.findCourse("CS101")).thenReturn(testCourse);
        when(groupMapper.toCreateGroup(groupRequest)).thenReturn(testGroup);
        when(groupRepository.save(any(Groups.class))).thenReturn(testGroup);

        Groups created = groupService.createGroup(groupRequest);

        assertNotNull(created.getGroupId());
        assertEquals(testCourse.getCourseId(), created.getCourse().getCourseId());
        assertEquals(leaderUser, created.getLeaderId());
        assertNotNull(created.getGroupMembers());
        assertNotNull(created.getGroupTags());
    }
}
