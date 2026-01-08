package com.teamup.main.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.teamup.main.dto.request.GroupRequest;
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
import com.teamup.main.repository.GroupRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("GroupService Comprehensive Unit Tests - Phase 2")
class GroupServiceTest {
    @Mock private GroupRepository groupRepository;
    @Mock private UserService userService;
    @Mock private CourseService courseService;
    @Mock private TagService tagService;
    @Mock private GroupMapper groupMapper;
    @Mock private UserMapper userMapper;
    @InjectMocks private GroupService groupService;

    private Groups testGroup;
    private Users testLeader;
    private Users testMember;
    private Users testMember2;
    private Courses testCourse;
    private GroupRequest groupRequest;

    @BeforeEach
    void setUp() {
        testLeader = new Users();
        testLeader.setUserId("leader123");

        testMember = new Users();
        testMember.setUserId("member123");

        testMember2 = new Users();
        testMember2.setUserId("member456");

        testCourse = new Courses();
        testCourse.setCourseId("course123");

        testGroup = new Groups();
        testGroup.setGroupId("group123");
        testGroup.setName("Test Group");
        testGroup.setMaxMembers(5);
        testGroup.setLeaderId(testLeader);
        testGroup.setCourse(testCourse);
        testGroup.setSemester(2451);
        testGroup.setGroupMembers(new HashSet<>());

        groupRequest = new GroupRequest();
        groupRequest.setGroupId("group123");
        groupRequest.setLeaderId("leader123");
        groupRequest.setCourseId("course123");
    }

    // ============ FIND GROUP TESTS ============
    @Nested
    @DisplayName("Find Group Tests")
    class FindGroupTests {
        @Test
        @DisplayName("Should find group by ID successfully")
        void testFindGroupSuccess() {
            when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
            
            Groups result = groupService.findGroup("group123");
            
            assertNotNull(result);
            assertEquals("group123", result.getGroupId());
            assertEquals("Test Group", result.getName());
            verify(groupRepository).findById("group123");
        }

        @Test
        @DisplayName("Should throw exception when group not found")
        void testFindGroupNotFound() {
            when(groupRepository.findById("invalid")).thenReturn(Optional.empty());
            
            AppException exception = assertThrows(AppException.class, 
                () -> groupService.findGroup("invalid"));
            assertEquals(ErrorCode.GROUP_NOT_FOUND, exception.getErrorCode());
        }
    }

    // ============ CREATE GROUP TESTS ============
    @Nested
    @DisplayName("Create Group Tests")
    class CreateGroupTests {
        @Test
        @DisplayName("Should create group with leader successfully")
        void testCreateGroupSuccess() {
            when(userService.findById("leader123")).thenReturn(testLeader);
            when(courseService.findCourse("course123")).thenReturn(testCourse);
            when(groupRepository.save(any(Groups.class))).thenReturn(testGroup);
            when(groupMapper.toCreateGroup(any())).thenReturn(testGroup);
            
            assertDoesNotThrow(() -> {
                groupService.createGroup(groupRequest);
            });
            
            verify(userService).findById("leader123");
            verify(courseService).findCourse("course123");
            verify(groupRepository, times(2)).save(any(Groups.class));
        }

        @Test
        @DisplayName("Should throw exception when leader not found")
        void testCreateGroupLeaderNotFound() {
            when(userService.findById("invalid")).thenThrow(new AppException(ErrorCode.USER_NOT_FOUND));
            
            groupRequest.setLeaderId("invalid");
            AppException exception = assertThrows(AppException.class,
                () -> groupService.createGroup(groupRequest));
            assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        @DisplayName("Should throw exception when course not found")
        void testCreateGroupCourseNotFound() {
            when(userService.findById("leader123")).thenReturn(testLeader);
            when(courseService.findCourse("invalid")).thenThrow(new AppException(ErrorCode.COURSE_NOT_FOUND));
            
            groupRequest.setCourseId("invalid");
            AppException exception = assertThrows(AppException.class,
                () -> groupService.createGroup(groupRequest));
            assertEquals(ErrorCode.COURSE_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        @DisplayName("Should add leader as first group member")
        void testCreateGroupAddLeaderAsMember() {
            when(userService.findById("leader123")).thenReturn(testLeader);
            when(courseService.findCourse("course123")).thenReturn(testCourse);
            when(groupRepository.save(any(Groups.class))).thenReturn(testGroup);
            when(groupMapper.toCreateGroup(any())).thenReturn(testGroup);
            
            groupService.createGroup(groupRequest);
            
            verify(groupRepository, times(2)).save(any(Groups.class));
        }
    }

    // ============ UPDATE GROUP TESTS ============
    @Nested
    @DisplayName("Update Group Tests")
    class UpdateGroupTests {
        @Test
        @DisplayName("Should update group with valid leader")
        void testUpdateGroupSuccess() {
            UserResponse leaderResponse = new UserResponse();
            leaderResponse.setUserId("leader123");
            
            GroupMember leaderMember = new GroupMember();
            leaderMember.setStatus(GroupStatus.JOINED);
            leaderMember.setUser(testLeader);
            
            Set<GroupMember> members = new HashSet<>();
            members.add(leaderMember);
            testGroup.setGroupMembers(members);
            
            when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
            when(userService.findById("leader123")).thenReturn(testLeader);
            when(userMapper.queryUser(any(), any())).thenReturn(leaderResponse);
            when(groupRepository.save(testGroup)).thenReturn(testGroup);
            when(courseService.findCourse("course123")).thenReturn(testCourse);
            doNothing().when(groupMapper).toUpdateGroup(any(), any());
            
            assertDoesNotThrow(() -> groupService.updateGroup(groupRequest));
            verify(groupRepository).save(testGroup);
        }

        @Test
        @DisplayName("Should throw exception when new leader not in group")
        void testUpdateGroupLeaderNotInGroup() {
            testGroup.setGroupMembers(new HashSet<>());
            
            when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
            when(userService.findById("newLeader")).thenReturn(new Users());
            
            groupRequest.setLeaderId("newLeader");
            AppException exception = assertThrows(AppException.class,
                () -> groupService.updateGroup(groupRequest));
            assertEquals(ErrorCode.USER_NOT_IN_GROUP, exception.getErrorCode());
        }
    }

    // ============ GET SIZE TESTS ============
    @Nested
    @DisplayName("Get Size Tests")
    class GetSizeTests {
        @Test
        @DisplayName("Should return zero for empty group")
        void testGetSizeEmpty() {
            testGroup.setGroupMembers(new HashSet<>());
            when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
            
            int size = groupService.getSize("group123");
            assertEquals(0, size);
        }

        @Test
        @DisplayName("Should return size filtering out non-JOINED members")
        void testGetSizeFiltersNonJoined() {
            Set<GroupMember> members = new HashSet<>();
            
            GroupMember member1 = new GroupMember();
            member1.setStatus(GroupStatus.JOINED);
            members.add(member1);
            
            GroupMember member2 = new GroupMember();
            member2.setStatus(GroupStatus.WAITING_APPROVAL);
            members.add(member2);
            
            testGroup.setGroupMembers(members);
            when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
            
            int size = groupService.getSize("group123");
            assertEquals(1, size);
        }

        @Test
        @DisplayName("Should not count REJECTED or LEFT members")
        void testGetSizeExcludesRejectedLeft() {
            Set<GroupMember> members = new HashSet<>();
            
            GroupMember joined = new GroupMember();
            joined.setStatus(GroupStatus.JOINED);
            members.add(joined);
            
            GroupMember rejected = new GroupMember();
            rejected.setStatus(GroupStatus.REJECTED);
            members.add(rejected);
            
            GroupMember left = new GroupMember();
            left.setStatus(GroupStatus.LEFT);
            members.add(left);
            
            testGroup.setGroupMembers(members);
            when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
            
            int size = groupService.getSize("group123");
            assertEquals(1, size);
        }
    }

    // ============ DELETE GROUP TESTS ============
    @Nested
    @DisplayName("Delete Group Tests")
    class DeleteGroupTests {
        @Test
        @DisplayName("Should delete group when it exists")
        void testDeleteGroupSuccess() {
            when(groupRepository.existsById("group123")).thenReturn(true);
            
            assertDoesNotThrow(() -> groupService.deleteGroup("group123"));
            verify(groupRepository).deleteById("group123");
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent group")
        void testDeleteGroupNotFound() {
            when(groupRepository.existsById("invalid")).thenReturn(false);
            
            AppException exception = assertThrows(AppException.class, 
                () -> groupService.deleteGroup("invalid"));
            assertEquals(ErrorCode.GROUP_NOT_FOUND, exception.getErrorCode());
            verify(groupRepository, never()).deleteById("invalid");
        }
    }

    // ============ GET CURRENT SEMESTER TESTS ============
    @Nested
    @DisplayName("Semester Tests")
    class SemesterTests {
        @Test
        @DisplayName("Should calculate semester correctly")
        void testGetCurrentSemester() {
            int semester = groupService.getCurrentSemester();
            assertNotNull(semester);
            assertTrue(semester > 0);
        }

        @Test
        @DisplayName("Semester format should be year*10 + semester_number")
        void testSemesterFormat() {
            int semester = groupService.getCurrentSemester();
            int year = semester / 10;
            int sem = semester % 10;
            
            assertTrue(year >= 25 && year <= 27, "Year should be 2-digit: " + year);
            assertTrue(sem >= 1 && sem <= 3, "Semester should be 1-3: " + sem);
        }
    }

    // ============ GET MEMBERS TESTS ============
    @Nested
    @DisplayName("Get Members Tests")
    class GetMembersTests {
        @Test
        @DisplayName("Should return only JOINED members")
        void testGetMembersFiltersJoined() {
            GroupMember joinedMember = new GroupMember();
            joinedMember.setStatus(GroupStatus.JOINED);
            joinedMember.setUser(testMember);
            
            Set<GroupMember> members = new HashSet<>();
            members.add(joinedMember);
            
            GroupMember waitingMember = new GroupMember();
            waitingMember.setStatus(GroupStatus.WAITING_APPROVAL);
            waitingMember.setUser(new Users());
            members.add(waitingMember);
            
            testGroup.setGroupMembers(members);
            when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
            when(userMapper.queryUser(any(), eq(testMember))).thenReturn(new UserResponse());
            
            List<UserResponse> result = groupService.getMembers("group123");
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should return empty list when no JOINED members")
        void testGetMembersEmpty() {
            GroupMember waitingMember = new GroupMember();
            waitingMember.setStatus(GroupStatus.WAITING_APPROVAL);
            waitingMember.setUser(new Users());
            
            Set<GroupMember> members = new HashSet<>();
            members.add(waitingMember);
            testGroup.setGroupMembers(members);
            
            when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
            
            List<UserResponse> result = groupService.getMembers("group123");
            assertTrue(result.isEmpty());
        }
    }

    // ============ ADD MEMBER TESTS ============
    @Nested
    @DisplayName("Add Member Tests")
    class AddMemberTests {
        @Test
        @DisplayName("Should reject adding user already in group")
        void testAddMemberUserAlreadyInGroup() {
            testGroup.setGroupMembers(new HashSet<>());
            when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
            when(userService.findAllById(List.of("member123"))).thenReturn(List.of(testMember));
            when(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
                "member123", "group123", GroupStatus.JOINED)).thenReturn(true);
            
            AppException exception = assertThrows(AppException.class,
                () -> groupService.addMember("group123", List.of("member123")));
            assertEquals(ErrorCode.USER_ALREADY_IN_GROUP, exception.getErrorCode());
        }

        @Test
        @DisplayName("Should throw exception with empty user list")
        void testAddMemberEmptyList() {
            testGroup.setGroupMembers(new HashSet<>());
            when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
            when(userService.findAllById(List.of())).thenReturn(List.of());
            
            AppException exception = assertThrows(AppException.class,
                () -> groupService.addMember("group123", List.of()));
            assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        @DisplayName("Should add multiple members successfully")
        void testAddMultipleMembers() {
            testGroup.setGroupMembers(new HashSet<>());
            when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
            when(userService.findAllById(List.of("member123", "member456")))
                .thenReturn(List.of(testMember, testMember2));
            when(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
                anyString(), eq("group123"), eq(GroupStatus.JOINED))).thenReturn(false);
            when(groupRepository.findGroupWithJoinedMember(anyInt(), eq("course123"), anyString()))
                .thenReturn(null);
            when(groupRepository.save(testGroup)).thenReturn(testGroup);
            
            Groups result = groupService.addMember("group123", List.of("member123", "member456"));
            
            assertNotNull(result);
            verify(groupRepository).save(testGroup);
        }
    }

    // ============ JOIN REQUEST TESTS ============
    @Nested
    @DisplayName("Join Request Tests")
    class JoinRequestTests {
        @Test
        @DisplayName("Should reject join request from user already in group")
        void testJoinRequestUserAlreadyInGroup() {
            testGroup.setGroupMembers(new HashSet<>());
            when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
            when(userService.findById("member123")).thenReturn(testMember);
            when(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
                "member123", "group123", GroupStatus.JOINED)).thenReturn(true);
            
            AppException exception = assertThrows(AppException.class,
                () -> groupService.joinRequest("group123", "member123", "Join please"));
            assertEquals(ErrorCode.USER_ALREADY_IN_GROUP, exception.getErrorCode());
        }

        @Test
        @DisplayName("Should accept join request successfully")
        void testJoinRequestSuccess() {
            testGroup.setGroupMembers(new HashSet<>());
            testGroup.setMaxMembers(5);
            when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
            when(userService.findById("member123")).thenReturn(testMember);
            when(groupRepository.existsByGroupMembers_Id_SecondIdAndGroupIdAndGroupMembers_Status(
                "member123", "group123", GroupStatus.JOINED)).thenReturn(false);
            when(groupRepository.findGroupWithJoinedMember(anyInt(), eq("course123"), eq("member123")))
                .thenReturn(null);
            when(groupRepository.save(testGroup)).thenReturn(testGroup);
            
            assertDoesNotThrow(() -> 
                groupService.joinRequest("group123", "member123", "I want to join"));
            verify(groupRepository).save(testGroup);
        }

        @Test
        @DisplayName("Should handle join request from pending approval status")
        void testJoinRequestFromPendingApproval() {
            Set<GroupMember> members = new HashSet<>();
            GroupMember pendingMember = new GroupMember();
            pendingMember.setStatus(GroupStatus.PENDING_APPROVAL);
            pendingMember.setUser(testMember);
            members.add(pendingMember);
            testGroup.setGroupMembers(members);
            
            when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
            when(userService.findById("member123")).thenReturn(testMember);
            
            assertDoesNotThrow(() -> 
                groupService.joinRequest("group123", "member123", "Join request"));
        }
    }

    // ============ IS JOIN ANOTHER GROUP TESTS ============
    @Nested
    @DisplayName("Join Another Group Tests")
    class IsJoinAnotherGroupTests {
        @Test
        @DisplayName("Should return true when user in another group with same course")
        void testIsJoinAnotherGroupTrue() {
            when(groupRepository.findGroupWithJoinedMember(anyInt(), eq("course123"), eq("member123")))
                .thenReturn(testGroup);
            
            Boolean result = groupService.isJoinAnotherGroupWithSameCourse("member123", "course123");
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when user not in another group")
        void testIsJoinAnotherGroupFalse() {
            when(groupRepository.findGroupWithJoinedMember(anyInt(), eq("course123"), eq("member123")))
                .thenReturn(null);
            
            Boolean result = groupService.isJoinAnotherGroupWithSameCourse("member123", "course123");
            assertFalse(result);
        }
    }

    // ============ KICK OR OUT GROUP TESTS ============
    @Nested
    @DisplayName("Kick Or Out Group Tests")
    class KickOrOutGroupTests {
        @Test
        @DisplayName("Should reject kicking the leader")
        void testKickLeaderError() {
            testGroup.setGroupMembers(new HashSet<>());
            when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
            
            AppException exception = assertThrows(AppException.class,
                () -> groupService.kickOrOutGroup("group123", "leader123", true));
            assertEquals(ErrorCode.NO_LEADER, exception.getErrorCode());
        }

        @Test
        @DisplayName("Should kick member successfully")
        void testKickMemberSuccess() {
            GroupMember member = new GroupMember();
            member.setStatus(GroupStatus.JOINED);
            member.setUser(testMember);
            
            Set<GroupMember> members = new HashSet<>();
            members.add(member);
            testGroup.setGroupMembers(members);
            
            when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
            when(groupRepository.save(testGroup)).thenReturn(testGroup);
            
            Groups result = groupService.kickOrOutGroup("group123", "member123", true);
            
            assertNotNull(result);
            assertEquals(GroupStatus.REMOVED, member.getStatus());
            verify(groupRepository).save(testGroup);
        }

        @Test
        @DisplayName("Should allow member to leave successfully")
        void testMemberLeaveSuccess() {
            GroupMember member = new GroupMember();
            member.setStatus(GroupStatus.JOINED);
            member.setUser(testMember);
            
            Set<GroupMember> members = new HashSet<>();
            members.add(member);
            testGroup.setGroupMembers(members);
            
            when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
            when(groupRepository.save(testGroup)).thenReturn(testGroup);
            
            Groups result = groupService.kickOrOutGroup("group123", "member123", false);
            
            assertNotNull(result);
            assertEquals(GroupStatus.LEFT, member.getStatus());
            verify(groupRepository).save(testGroup);
        }
    }

    // ============ ACCEPT/REJECT JOIN REQUEST TESTS ============
    @Nested
    @DisplayName("Accept/Reject Join Request Tests")
    class AcceptRejectJoinTests {
        @Test
        @DisplayName("Should successfully reject join request")
        void testRejectJoinRequest() {
            GroupMember member = new GroupMember();
            member.setStatus(GroupStatus.WAITING_APPROVAL);
            member.setUser(testMember);
            
            Set<GroupMember> members = new HashSet<>();
            members.add(member);
            testGroup.setGroupMembers(members);
            
            when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
            when(groupRepository.save(testGroup)).thenReturn(testGroup);
            
            groupService.rejectJoinRequest("group123", "member123");
            assertEquals(GroupStatus.REJECTED, member.getStatus());
            verify(groupRepository).save(testGroup);
        }

        @Test
        @DisplayName("Should successfully accept join request")
        void testAcceptJoinRequest() {
            GroupMember member = new GroupMember();
            member.setStatus(GroupStatus.WAITING_APPROVAL);
            member.setUser(testMember);
            
            Set<GroupMember> members = new HashSet<>();
            members.add(member);
            testGroup.setGroupMembers(members);
            
            when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
            when(groupRepository.save(testGroup)).thenReturn(testGroup);
            
            groupService.acceptJoinRequest("group123", "member123");
            assertEquals(GroupStatus.JOINED, member.getStatus());
            verify(groupRepository).save(testGroup);
        }

        @Test
        @DisplayName("Should accept pending approval requests")
        void testAcceptPendingApprovalRequest() {
            GroupMember member = new GroupMember();
            member.setStatus(GroupStatus.PENDING_APPROVAL);
            member.setUser(testMember);
            
            Set<GroupMember> members = new HashSet<>();
            members.add(member);
            testGroup.setGroupMembers(members);
            
            when(groupRepository.findById("group123")).thenReturn(Optional.of(testGroup));
            when(groupRepository.save(testGroup)).thenReturn(testGroup);
            
            groupService.acceptJoinRequest("group123", "member123");
            assertEquals(GroupStatus.JOINED, member.getStatus());
            verify(groupRepository).save(testGroup);
        }
    }
}
