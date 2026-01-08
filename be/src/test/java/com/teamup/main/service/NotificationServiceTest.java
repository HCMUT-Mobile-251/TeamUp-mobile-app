package com.teamup.main.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.teamup.main.enums.ErrorCode;
import com.teamup.main.enums.GroupStatus;
import com.teamup.main.exception.AppException;
import com.teamup.main.model.GroupMember;
import com.teamup.main.model.Groups;
import com.teamup.main.model.PairId;
import com.teamup.main.repository.GroupMemberRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService Tests - Phase 6")
class NotificationServiceTest {

    @Mock
    private GroupMemberRepository groupMemberRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Groups testGroup;
    private GroupMember testNotification;
    private PairId testPairId;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testGroup = new Groups();
        testGroup.setGroupId("group1");
        testGroup.setName("Java Programming");

        testPairId = new PairId();
        testPairId.setFirstId("user1");
        testPairId.setSecondId("group1");

        testNotification = new GroupMember();
        testNotification.setId(testPairId);
        testNotification.setGroup(testGroup);
        testNotification.setStatus(GroupStatus.PENDING_APPROVAL);
        testNotification.setDeleted(false);
        testNotification.setTime(java.time.Instant.now());
    }

    @Nested
    @DisplayName("Get Notifications By UserId Tests")
    class GetNotificationsByUserIdTests {

        @Test
        @DisplayName("Should retrieve notifications for valid user ID with pagination")
        void testGetNotificationByUserIdSuccess() {
            // Arrange
            String userId = "user1";
            int page = 0;
            int size = 10;
            Pageable pageable = PageRequest.of(page, size);
            List<GroupMember> notificationList = new ArrayList<>();
            notificationList.add(testNotification);
            Page<GroupMember> expectedPage = new PageImpl<>(notificationList, pageable, 1);

            when(groupMemberRepository.findById_FirstIdAndIsDeletedOrderByTimeDesc(userId, false, pageable))
                    .thenReturn(expectedPage);

            // Act
            Page<GroupMember> result = notificationService.getNotificationByUserId(userId, page, size);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(testNotification.getId().getFirstId(), result.getContent().get(0).getId().getFirstId());
            verify(groupMemberRepository, times(1)).findById_FirstIdAndIsDeletedOrderByTimeDesc(userId, false,
                    pageable);
        }

        @Test
        @DisplayName("Should return empty page when user has no notifications")
        void testGetNotificationByUserIdEmpty() {
            // Arrange
            String userId = "user2";
            int page = 0;
            int size = 10;
            Pageable pageable = PageRequest.of(page, size);
            Page<GroupMember> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

            when(groupMemberRepository.findById_FirstIdAndIsDeletedOrderByTimeDesc(userId, false, pageable))
                    .thenReturn(emptyPage);

            // Act
            Page<GroupMember> result = notificationService.getNotificationByUserId(userId, page, size);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
            assertTrue(result.getContent().isEmpty());
        }

        @Test
        @DisplayName("Should respect pagination parameters")
        void testGetNotificationByUserIdWithPagination() {
            // Arrange
            String userId = "user1";
            int page = 1;
            int size = 5;
            Pageable pageable = PageRequest.of(page, size);
            List<GroupMember> notificationList = new ArrayList<>();
            Page<GroupMember> expectedPage = new PageImpl<>(notificationList, pageable, 0);

            when(groupMemberRepository.findById_FirstIdAndIsDeletedOrderByTimeDesc(userId, false, pageable))
                    .thenReturn(expectedPage);

            // Act
            Page<GroupMember> result = notificationService.getNotificationByUserId(userId, page, size);

            // Assert
            assertNotNull(result);
            verify(groupMemberRepository, times(1)).findById_FirstIdAndIsDeletedOrderByTimeDesc(userId, false,
                    pageable);
        }

        @Test
        @DisplayName("Should filter only non-deleted notifications")
        void testGetNotificationFiltersDeletedNotifications() {
            // Arrange
            String userId = "user1";
            int page = 0;
            int size = 10;
            Pageable pageable = PageRequest.of(page, size);

            // Verify that the method is called with isDeleted=false
            when(groupMemberRepository.findById_FirstIdAndIsDeletedOrderByTimeDesc(userId, false, pageable))
                    .thenReturn(new PageImpl<>(new ArrayList<>(), pageable, 0));

            // Act
            notificationService.getNotificationByUserId(userId, page, size);

            // Assert
            verify(groupMemberRepository, times(1)).findById_FirstIdAndIsDeletedOrderByTimeDesc(userId, false,
                    pageable);
        }
    }

    @Nested
    @DisplayName("Find Notification By Name Tests")
    class FindNotificationByNameTests {

        @Test
        @DisplayName("Should find notifications by group name containing search term")
        void testFindNotificationByNameSuccess() {
            // Arrange
            String userId = "user1";
            String nameGroup = "Java";
            int page = 0;
            int size = 10;
            Pageable pageable = PageRequest.of(page, size);
            List<GroupMember> notificationList = new ArrayList<>();
            notificationList.add(testNotification);
            Page<GroupMember> expectedPage = new PageImpl<>(notificationList, pageable, 1);

            when(groupMemberRepository.findById_FirstIdAndGroup_NameContainingIgnoreCase(userId, nameGroup, pageable))
                    .thenReturn(expectedPage);

            // Act
            Page<GroupMember> result = notificationService.findNotificationByNameByUserId(userId, nameGroup, page,
                    size);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            verify(groupMemberRepository, times(2))
                    .findById_FirstIdAndGroup_NameContainingIgnoreCase(userId, nameGroup, pageable);
        }

        @Test
        @DisplayName("Should throw exception when notification not found by name")
        void testFindNotificationByNameNotFound() {
            // Arrange
            String userId = "user1";
            String nameGroup = "NonExistent";
            int page = 0;
            int size = 10;
            Pageable pageable = PageRequest.of(page, size);
            Page<GroupMember> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

            when(groupMemberRepository.findById_FirstIdAndGroup_NameContainingIgnoreCase(userId, nameGroup, pageable))
                    .thenReturn(emptyPage);

            // Act & Assert
            AppException exception = assertThrows(AppException.class,
                    () -> notificationService.findNotificationByNameByUserId(userId, nameGroup, page, size));
            assertEquals(ErrorCode.NOTI_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        @DisplayName("Should perform case-insensitive search")
        void testFindNotificationByNameCaseInsensitive() {
            // Arrange
            String userId = "user1";
            String nameGroup = "java";
            int page = 0;
            int size = 10;
            Pageable pageable = PageRequest.of(page, size);
            List<GroupMember> notificationList = new ArrayList<>();
            notificationList.add(testNotification);
            Page<GroupMember> expectedPage = new PageImpl<>(notificationList, pageable, 1);

            when(groupMemberRepository.findById_FirstIdAndGroup_NameContainingIgnoreCase(userId, nameGroup, pageable))
                    .thenReturn(expectedPage);

            // Act
            Page<GroupMember> result = notificationService.findNotificationByNameByUserId(userId, nameGroup, page,
                    size);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("Should support pagination when searching by name")
        void testFindNotificationByNameWithPagination() {
            // Arrange
            String userId = "user1";
            String nameGroup = "Java";
            int page = 1;
            int size = 5;
            Pageable pageable = PageRequest.of(page, size);
            List<GroupMember> notificationList = new ArrayList<>();
            Page<GroupMember> expectedPage = new PageImpl<>(notificationList, pageable, 0);

            when(groupMemberRepository.findById_FirstIdAndGroup_NameContainingIgnoreCase(userId, nameGroup, pageable))
                    .thenReturn(expectedPage);

            // Act & Assert
            AppException exception = assertThrows(AppException.class,
                    () -> notificationService.findNotificationByNameByUserId(userId, nameGroup, page, size));
            assertEquals(ErrorCode.NOTI_NOT_FOUND, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Find Notification By ID Tests")
    class FindNotificationByIdTests {

        @Test
        @DisplayName("Should find notification by PairId with non-JOINED status")
        void testFindNotificationByIdSuccess() {
            // Arrange
            testNotification.setStatus(GroupStatus.PENDING_APPROVAL);
            when(groupMemberRepository.findByIdAndStatusNot(testPairId, GroupStatus.JOINED))
                    .thenReturn(testNotification);

            // Act
            GroupMember result = notificationService.findNotificationById(testPairId);

            // Assert
            assertNotNull(result);
            assertEquals(testPairId.getFirstId(), result.getId().getFirstId());
            assertEquals(GroupStatus.PENDING_APPROVAL, result.getStatus());
            verify(groupMemberRepository, times(2)).findByIdAndStatusNot(testPairId, GroupStatus.JOINED);
        }

        @Test
        @DisplayName("Should throw exception when notification not found")
        void testFindNotificationByIdNotFound() {
            // Arrange
            when(groupMemberRepository.findByIdAndStatusNot(testPairId, GroupStatus.JOINED)).thenReturn(null);

            // Act & Assert
            AppException exception = assertThrows(AppException.class,
                    () -> notificationService.findNotificationById(testPairId));
            assertEquals(ErrorCode.NOTI_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        @DisplayName("Should handle WAITING_APPROVAL status notifications")
        void testFindNotificationWithWaitingApprovalStatus() {
            // Arrange
            testNotification.setStatus(GroupStatus.WAITING_APPROVAL);
            when(groupMemberRepository.findByIdAndStatusNot(testPairId, GroupStatus.JOINED))
                    .thenReturn(testNotification);

            // Act
            GroupMember result = notificationService.findNotificationById(testPairId);

            // Assert
            assertNotNull(result);
            assertEquals(GroupStatus.WAITING_APPROVAL, result.getStatus());
        }

        @Test
        @DisplayName("Should exclude JOINED status notifications")
        void testFindNotificationExcludesJoinedStatus() {
            // Arrange - notification with JOINED status should not be returned
            when(groupMemberRepository.findByIdAndStatusNot(testPairId, GroupStatus.JOINED)).thenReturn(null);

            // Act & Assert
            AppException exception = assertThrows(AppException.class,
                    () -> notificationService.findNotificationById(testPairId));
            assertEquals(ErrorCode.NOTI_NOT_FOUND, exception.getErrorCode());
            verify(groupMemberRepository, times(1)).findByIdAndStatusNot(testPairId, GroupStatus.JOINED);
        }
    }

    @Nested
    @DisplayName("Delete Notification Tests")
    class DeleteNotificationTests {

        @Test
        @DisplayName("Should delete notification successfully")
        void testDeleteNotificationSuccess() {
            // Arrange
            when(groupMemberRepository.findByIdAndStatusNot(testPairId, GroupStatus.JOINED))
                    .thenReturn(testNotification);

            // Act
            notificationService.deleteNotification(testPairId);

            // Assert
            verify(groupMemberRepository, times(2)).findByIdAndStatusNot(testPairId, GroupStatus.JOINED);
            verify(groupMemberRepository, times(1)).deleteById(testPairId);
        }

        @Test
        @DisplayName("Should throw exception when trying to delete non-existent notification")
        void testDeleteNotificationNotFound() {
            // Arrange
            when(groupMemberRepository.findByIdAndStatusNot(testPairId, GroupStatus.JOINED)).thenReturn(null);

            // Act & Assert
            AppException exception = assertThrows(AppException.class,
                    () -> notificationService.deleteNotification(testPairId));
            assertEquals(ErrorCode.NOTI_NOT_FOUND, exception.getErrorCode());
            verify(groupMemberRepository, never()).deleteById(testPairId);
        }

        @Test
        @DisplayName("Should handle deletion of notifications with pending approval status")
        void testDeletePendingApprovalNotification() {
            // Arrange
            testNotification.setStatus(GroupStatus.PENDING_APPROVAL);
            when(groupMemberRepository.findByIdAndStatusNot(testPairId, GroupStatus.JOINED))
                    .thenReturn(testNotification);

            // Act
            notificationService.deleteNotification(testPairId);

            // Assert
            verify(groupMemberRepository, times(1)).deleteById(testPairId);
        }

        @Test
        @DisplayName("Should call repository deleteById with correct PairId")
        void testDeleteNotificationCallsRepository() {
            // Arrange
            when(groupMemberRepository.findByIdAndStatusNot(testPairId, GroupStatus.JOINED))
                    .thenReturn(testNotification);

            // Act
            notificationService.deleteNotification(testPairId);

            // Assert
            verify(groupMemberRepository).deleteById(argThat(pairId -> pairId.getFirstId().equals("user1")
                    && pairId.getSecondId().equals("group1")));
        }
    }

    @Nested
    @DisplayName("Integration Scenario Tests")
    class IntegrationScenarioTests {

        @Test
        @DisplayName("Should handle complete notification workflow: retrieve, filter, and delete")
        void testCompleteNotificationWorkflow() {
            // Arrange
            String userId = "user1";
            int page = 0;
            int size = 10;
            Pageable pageable = PageRequest.of(page, size);
            List<GroupMember> notificationList = new ArrayList<>();
            notificationList.add(testNotification);
            Page<GroupMember> expectedPage = new PageImpl<>(notificationList, pageable, 1);

            when(groupMemberRepository.findById_FirstIdAndIsDeletedOrderByTimeDesc(userId, false, pageable))
                    .thenReturn(expectedPage);
            when(groupMemberRepository.findByIdAndStatusNot(testPairId, GroupStatus.JOINED))
                    .thenReturn(testNotification);

            // Act
            Page<GroupMember> notifications = notificationService.getNotificationByUserId(userId, page, size);
            GroupMember foundNotification = notificationService.findNotificationById(testPairId);
            notificationService.deleteNotification(testPairId);

            // Assert
            assertEquals(1, notifications.getTotalElements());
            assertNotNull(foundNotification);
            verify(groupMemberRepository, times(1)).deleteById(testPairId);
        }

        @Test
        @DisplayName("Should handle multiple notifications for same user")
        void testMultipleNotificationsForUser() {
            // Arrange
            String userId = "user1";
            int page = 0;
            int size = 10;
            Pageable pageable = PageRequest.of(page, size);

            GroupMember notification2 = new GroupMember();
            Groups group2 = new Groups();
            group2.setGroupId("group2");
            group2.setName("Python Programming");
            PairId pairId2 = new PairId();
            pairId2.setFirstId("user1");
            pairId2.setSecondId("group2");
            notification2.setId(pairId2);
            notification2.setGroup(group2);
            notification2.setStatus(GroupStatus.REJECTED);

            List<GroupMember> notificationList = new ArrayList<>();
            notificationList.add(testNotification);
            notificationList.add(notification2);
            Page<GroupMember> expectedPage = new PageImpl<>(notificationList, pageable, 2);

            when(groupMemberRepository.findById_FirstIdAndIsDeletedOrderByTimeDesc(userId, false, pageable))
                    .thenReturn(expectedPage);

            // Act
            Page<GroupMember> result = notificationService.getNotificationByUserId(userId, page, size);

            // Assert
            assertEquals(2, result.getTotalElements());
            assertEquals(2, result.getContent().size());
        }

        @Test
        @DisplayName("Should handle search returning multiple results")
        void testSearchNotificationReturnsMultipleResults() {
            // Arrange
            String userId = "user1";
            String nameGroup = "Java";
            int page = 0;
            int size = 10;
            Pageable pageable = PageRequest.of(page, size);

            GroupMember notification2 = new GroupMember();
            Groups group2 = new Groups();
            group2.setGroupId("group2");
            group2.setName("Java Advanced");
            PairId pairId2 = new PairId();
            pairId2.setFirstId("user1");
            pairId2.setSecondId("group2");
            notification2.setId(pairId2);
            notification2.setGroup(group2);
            notification2.setStatus(GroupStatus.PENDING_APPROVAL);

            List<GroupMember> notificationList = new ArrayList<>();
            notificationList.add(testNotification);
            notificationList.add(notification2);
            Page<GroupMember> expectedPage = new PageImpl<>(notificationList, pageable, 2);

            when(groupMemberRepository.findById_FirstIdAndGroup_NameContainingIgnoreCase(userId, nameGroup, pageable))
                    .thenReturn(expectedPage);

            // Act
            Page<GroupMember> result = notificationService.findNotificationByNameByUserId(userId, nameGroup, page,
                    size);

            // Assert
            assertEquals(2, result.getTotalElements());
        }
    }
}
