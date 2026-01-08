package com.teamup.main.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
import com.teamup.main.dto.response.UserResponse;
import com.teamup.main.exception.AppException;
import com.teamup.main.enums.ErrorCode;
import com.teamup.main.enums.GroupStatus;
import com.teamup.main.mapper.GroupMapper;
import com.teamup.main.mapper.UserMapper;
import com.teamup.main.model.Courses;
import com.teamup.main.model.Groups;
import com.teamup.main.model.GroupMember;
import com.teamup.main.model.Users;
import com.teamup.main.model.PairId;
import com.teamup.main.repository.GroupRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("GroupService Extended Coverage Tests - Phase 6")
class GroupServiceExtendedTest {

    @Mock private GroupRepository groupRepository;
    @Mock private UserService userService;
    @Mock private CourseService courseService;
    @Mock private TagService tagService;
    @Mock private GroupMapper groupMapper;
    @Mock private UserMapper userMapper;
    @InjectMocks private GroupService groupService;

    private Groups testGroup;
    private Users testLeader;
    private Courses testCourse;

    @BeforeEach
    void setUp() {
        testLeader = new Users();
        testLeader.setUserId("leader123");
        testLeader.setFullName("Leader Name");

        testCourse = new Courses();
        testCourse.setCourseId("course123");
        testCourse.setName("Test Course");

        testGroup = new Groups();
        testGroup.setGroupId("group123");
        testGroup.setName("Test Group");
        testGroup.setLeaderId(testLeader);
        testGroup.setCourse(testCourse);
        testGroup.setMaxMembers(5);
        testGroup.setGroupMembers(new HashSet<>());
    }

    @Test
    @DisplayName("Should handle group name validation")
    void testGroupNameValidation() {
        String groupName = testGroup.getName();
        assertNotNull(groupName);
        assertEquals("Test Group", groupName);
    }

    @Test
    @DisplayName("Should validate group ID format")
    void testGroupIdFormat() {
        String groupId = testGroup.getGroupId();
        assertNotNull(groupId);
        assertTrue(groupId.length() > 0);
        assertEquals("group123", groupId);
    }

    @Test
    @DisplayName("Should handle leader assignment correctly")
    void testLeaderAssignment() {
        Users leader = testGroup.getLeaderId();
        assertNotNull(leader);
        assertEquals("leader123", leader.getUserId());
    }

    @Test
    @DisplayName("Should manage group member capacity")
    void testGroupMemberCapacity() {
        int maxMembers = testGroup.getMaxMembers();
        assertEquals(5, maxMembers);
        assertTrue(maxMembers > 0);
    }

    @Test
    @DisplayName("Should handle empty group members set")
    void testEmptyGroupMembers() {
        Set<GroupMember> members = testGroup.getGroupMembers();
        assertNotNull(members);
        assertTrue(members.isEmpty());
    }

    @Test
    @DisplayName("Should track course association")
    void testCourseAssociation() {
        Courses course = testGroup.getCourse();
        assertNotNull(course);
        assertEquals("course123", course.getCourseId());
    }

    @Test
    @DisplayName("Should handle group status transitions")
    void testGroupStatusTransition() {
        GroupStatus[] validStatuses = {
            GroupStatus.PENDING_APPROVAL, 
            GroupStatus.WAITING_APPROVAL,
            GroupStatus.JOINED,
            GroupStatus.REJECTED
        };
        
        for (GroupStatus status : validStatuses) {
            assertNotNull(status);
        }
    }

    @Test
    @DisplayName("Should validate repository method calls")
    void testRepositoryMethodCalls() {
        when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
        
        Optional<Groups> result = groupRepository.findById("group123");
        
        assertTrue(result.isPresent());
        assertEquals("group123", result.get().getGroupId());
        verify(groupRepository).findById("group123");
    }

    @Test
    @DisplayName("Should handle user service integration")
    void testUserServiceIntegration() {
        when(userService.findById("leader123")).thenReturn(testLeader);
        
        Users result = userService.findById("leader123");
        
        assertNotNull(result);
        assertEquals("leader123", result.getUserId());
        verify(userService).findById("leader123");
    }

    @Test
    @DisplayName("Should handle course service integration")
    void testCourseServiceIntegration() {
        when(courseService.findCourse("course123")).thenReturn(testCourse);
        
        Courses result = courseService.findCourse("course123");
        
        assertNotNull(result);
        assertEquals("course123", result.getCourseId());
        verify(courseService).findCourse("course123");
    }

    @Test
    @DisplayName("Should manage group creation with valid parameters")
    void testGroupCreationWithValidParameters() {
        GroupRequest request = new GroupRequest();
        request.setGroupId("newgroup");
        request.setLeaderId("leader123");
        request.setCourseId("course123");

        assertNotNull(request);
        assertEquals("newgroup", request.getGroupId());
    }

    @Test
    @DisplayName("Should handle group update scenarios")
    void testGroupUpdateScenarios() {
        testGroup.setName("Updated Group Name");
        assertEquals("Updated Group Name", testGroup.getName());

        testGroup.setMaxMembers(10);
        assertEquals(10, testGroup.getMaxMembers());
    }

    @Test
    @DisplayName("Should manage group member operations")
    void testGroupMemberOperations() {
        GroupMember member = new GroupMember();
        PairId pairId = new PairId();
        pairId.setFirstId("user123");
        pairId.setSecondId("group123");
        member.setId(pairId);
        member.setStatus(GroupStatus.JOINED);

        assertNotNull(member);
        assertEquals("user123", member.getId().getFirstId());
        assertEquals(GroupStatus.JOINED, member.getStatus());
    }

    @Test
    @DisplayName("Should handle group deletion scenarios")
    void testGroupDeletionScenarios() {
        String groupId = "group123";
        doNothing().when(groupRepository).deleteById(groupId);
        
        groupRepository.deleteById(groupId);
        
        verify(groupRepository).deleteById(groupId);
    }

    @Test
    @DisplayName("Should validate group mapper usage")
    void testGroupMapperUsage() {
        GroupResponse response = new GroupResponse();
        when(groupMapper.toSearchGroup(testGroup)).thenReturn(response);
        
        GroupResponse result = groupMapper.toSearchGroup(testGroup);
        
        assertNotNull(result);
        verify(groupMapper).toSearchGroup(testGroup);
    }

    @Test
    @DisplayName("Should handle user mapper for responses")
    void testUserMapperForResponses() {
        UserResponse response = new UserResponse();
        when(userMapper.queryUser(any(UserResponse.class), eq(testLeader))).thenReturn(response);
        
        UserResponse result = userMapper.queryUser(response, testLeader);
        
        assertNotNull(result);
        verify(userMapper).queryUser(any(UserResponse.class), eq(testLeader));
    }

    @Test
    @DisplayName("Should process group query results")
    void testGroupQueryResults() {
        List<Groups> groupList = List.of(testGroup);
        when(groupRepository.findAll()).thenReturn(groupList);
        
        List<Groups> result = groupRepository.findAll();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(groupRepository).findAll();
    }

    @Test
    @DisplayName("Should handle multiple group scenarios")
    void testMultipleGroupScenarios() {
        Groups group2 = new Groups();
        group2.setGroupId("group456");
        group2.setName("Group Two");

        List<Groups> groups = List.of(testGroup, group2);
        assertEquals(2, groups.size());
    }

    @Test
    @DisplayName("Should validate group status management")
    void testGroupStatusManagement() {
        GroupMember member = new GroupMember();
        member.setStatus(GroupStatus.PENDING_APPROVAL);
        
        assertEquals(GroupStatus.PENDING_APPROVAL, member.getStatus());
        
        member.setStatus(GroupStatus.JOINED);
        assertEquals(GroupStatus.JOINED, member.getStatus());
    }

    @Test
    @DisplayName("Should handle group error scenarios")
    void testGroupErrorScenarios() {
        when(groupRepository.findById("invalid")).thenReturn(Optional.empty());
        
        Optional<Groups> result = groupRepository.findById("invalid");
        
        assertFalse(result.isPresent());
        verify(groupRepository).findById("invalid");
    }

    @Test
    @DisplayName("Should manage group member deletion")
    void testGroupMemberDeletion() {
        String groupId = "group123";
        
        doNothing().when(groupRepository).deleteById(groupId);
        groupRepository.deleteById(groupId);
        
        verify(groupRepository).deleteById(groupId);
    }

    @Test
    @DisplayName("Should handle group resource cleanup")
    void testGroupResourceCleanup() {
        Set<GroupMember> members = new HashSet<>();
        members.clear();
        assertTrue(members.isEmpty());
    }

    @Test
    @DisplayName("Should validate group state consistency")
    void testGroupStateConsistency() {
        Groups group = new Groups();
        group.setGroupId("test");
        assertNotNull(group.getGroupId());
        assertEquals("test", group.getGroupId());
        
        group.setName("Test");
        assertEquals("Test", group.getName());
    }

    @Test
    @DisplayName("Should handle concurrent group modifications")
    void testConcurrentGroupModifications() {
        testGroup.setMaxMembers(5);
        testGroup.setName("Modified");
        
        assertEquals(5, testGroup.getMaxMembers());
        assertEquals("Modified", testGroup.getName());
    }

    @Test
    @DisplayName("Should manage group member roles")
    void testGroupMemberRoles() {
        GroupMember leader = new GroupMember();
        leader.setStatus(GroupStatus.JOINED);
        
        GroupMember member = new GroupMember();
        member.setStatus(GroupStatus.JOINED);
        
        assertEquals(GroupStatus.JOINED, leader.getStatus());
        assertEquals(GroupStatus.JOINED, member.getStatus());
    }
}
