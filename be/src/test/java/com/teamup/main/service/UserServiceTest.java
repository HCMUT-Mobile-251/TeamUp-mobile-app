package com.teamup.main.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.teamup.main.dto.request.GoogleAccount;
import com.teamup.main.dto.request.UserUpdateRequest;
import com.teamup.main.exception.AppException;
import com.teamup.main.enums.ErrorCode;
import com.teamup.main.mapper.UserMapper;
import com.teamup.main.model.PairId;
import com.teamup.main.model.Tags;
import com.teamup.main.model.UserTag;
import com.teamup.main.model.Users;
import com.teamup.main.repository.TagRepository;
import com.teamup.main.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Comprehensive Unit Tests - Phase 3")
class UserServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private TagRepository tagRepository;
    @InjectMocks private UserService userService;

    private Users testUser;
    private Users testUser2;
    private GoogleAccount googleAccount;
    private UserUpdateRequest updateRequest;
    private Tags testTag;

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setUserId("user123");
        testUser.setEmail("user@example.com");
        testUser.setStudentId("SV001");
        testUser.setFullName("Test User");

        testUser2 = new Users();
        testUser2.setUserId("user456");
        testUser2.setEmail("user2@example.com");
        testUser2.setStudentId("SV002");

        googleAccount = GoogleAccount.builder()
            .id("google_123")
            .email("user@example.com")
            .verified_email(true)
            .name("Test User")
            .given_name("Test")
            .family_name("User")
            .picture("https://example.com/pic.jpg")
            .hd("gmail.com")
            .build();

        updateRequest = new UserUpdateRequest();
        updateRequest.setStudentId("SV001");
        updateRequest.setPhoneNumber("0123456789");

        testTag = new Tags();
        testTag.setTagId("tag123");
        testTag.setTagName("Test Tag");
    }

    // ============ FIND USER TESTS ============
    @Nested
    @DisplayName("Find User Tests")
    class FindUserTests {
        @Test
        @DisplayName("Should find user by ID successfully")
        void testFindUserByIdSuccess() {
            when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
            
            Users result = userService.findById("user123");
            
            assertNotNull(result);
            assertEquals("user123", result.getUserId());
            assertEquals("Test User", result.getFullName());
            verify(userRepository).findById("user123");
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void testFindUserByIdNotFound() {
            when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());
            
            AppException exception = assertThrows(AppException.class,
                () -> userService.findById("nonexistent"));
            assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        }
    }

    // ============ CREATE USER TESTS ============
    @Nested
    @DisplayName("Create User Tests")
    class CreateUserTests {
        @Test
        @DisplayName("Should create new user from Google account")
        void testCreateNewUserSuccess() {
            when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
            when(userMapper.toUser(googleAccount)).thenReturn(testUser);
            when(userRepository.save(testUser)).thenReturn(testUser);
            
            Users result = userService.createUser(googleAccount);
            
            assertNotNull(result);
            assertEquals("user123", result.getUserId());
            verify(userRepository).findByEmail("user@example.com");
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Should return existing user if email already exists")
        void testCreateUserEmailAlreadyExists() {
            when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));
            
            Users result = userService.createUser(googleAccount);
            
            assertNotNull(result);
            assertEquals("user123", result.getUserId());
            verify(userRepository).findByEmail("user@example.com");
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should handle multiple users with different emails")
        void testCreateMultipleUsersWithDifferentEmails() {
            GoogleAccount account1 = GoogleAccount.builder()
                .id("google_1")
                .email("user1@example.com")
                .verified_email(true)
                .name("User One")
                .build();
            GoogleAccount account2 = GoogleAccount.builder()
                .id("google_2")
                .email("user2@example.com")
                .verified_email(true)
                .name("User Two")
                .build();

            when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.empty());
            when(userRepository.findByEmail("user2@example.com")).thenReturn(Optional.empty());
            when(userMapper.toUser(account1)).thenReturn(testUser);
            when(userMapper.toUser(account2)).thenReturn(testUser2);
            when(userRepository.save(testUser)).thenReturn(testUser);
            when(userRepository.save(testUser2)).thenReturn(testUser2);
            
            Users result1 = userService.createUser(account1);
            Users result2 = userService.createUser(account2);
            
            assertNotNull(result1);
            assertNotNull(result2);
            verify(userRepository, times(2)).save(any());
        }
    }

    // ============ UPDATE USER TESTS ============
    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {
        @Test
        @DisplayName("Should update user information successfully")
        void testUpdateUserSuccess() {
            when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
            doNothing().when(userMapper).updateUser(testUser, updateRequest);
            when(userRepository.save(testUser)).thenReturn(testUser);
            
            Users result = userService.updateUser("user123", updateRequest);
            
            assertNotNull(result);
            verify(userMapper).updateUser(testUser, updateRequest);
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Should throw exception when user not found on update")
        void testUpdateUserNotFound() {
            when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());
            
            AppException exception = assertThrows(AppException.class,
                () -> userService.updateUser("nonexistent", updateRequest));
            assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should update user with null fields")
        void testUpdateUserWithNullFields() {
            UserUpdateRequest emptyRequest = new UserUpdateRequest();
            when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
            doNothing().when(userMapper).updateUser(testUser, emptyRequest);
            when(userRepository.save(testUser)).thenReturn(testUser);
            
            Users result = userService.updateUser("user123", emptyRequest);
            
            assertNotNull(result);
            verify(userRepository).save(testUser);
        }
    }

    // ============ DELETE USER TESTS ============
    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {
        @Test
        @DisplayName("Should delete existing user successfully")
        void testDeleteUserSuccess() {
            when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
            doNothing().when(userRepository).deleteById("user123");
            
            assertDoesNotThrow(() -> userService.deleteUser("user123"));
            verify(userRepository).deleteById("user123");
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent user")
        void testDeleteUserNotFound() {
            when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());
            
            AppException exception = assertThrows(AppException.class,
                () -> userService.deleteUser("nonexistent"));
            assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
            verify(userRepository, never()).deleteById(any());
        }
    }

    // ============ GET USERS TESTS ============
    @Nested
    @DisplayName("Get Users Tests")
    class GetUsersTests {
        @Test
        @DisplayName("Should retrieve all users successfully")
        void testGetUsersSuccess() {
            List<Users> userList = List.of(testUser, testUser2);
            when(userRepository.findAll()).thenReturn(userList);
            
            List<Users> result = userService.getUsers();
            
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(userRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no users exist")
        void testGetUsersEmpty() {
            when(userRepository.findAll()).thenReturn(List.of());
            
            List<Users> result = userService.getUsers();
            
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    // ============ FIND ALL BY ID TESTS ============
    @Nested
    @DisplayName("Find All By ID Tests")
    class FindAllByIdTests {
        @Test
        @DisplayName("Should find multiple users by IDs")
        void testFindAllByIdSuccess() {
            List<String> userIds = List.of("user123", "user456");
            List<Users> users = List.of(testUser, testUser2);
            when(userRepository.findAllById(userIds)).thenReturn(users);
            
            List<Users> result = userService.findAllById(userIds);
            
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(userRepository).findAllById(userIds);
        }

        @Test
        @DisplayName("Should handle empty ID list")
        void testFindAllByIdEmptyList() {
            when(userRepository.findAllById(List.of())).thenReturn(List.of());
            
            List<Users> result = userService.findAllById(List.of());
            
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should handle partial ID matches")
        void testFindAllByIdPartialMatches() {
            List<String> userIds = List.of("user123", "nonexistent");
            List<Users> users = List.of(testUser);
            when(userRepository.findAllById(userIds)).thenReturn(users);
            
            List<Users> result = userService.findAllById(userIds);
            
            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }

    // ============ GET USERS BY STUDENT ID TESTS ============
    @Nested
    @DisplayName("Get Users By Student ID Tests")
    class GetUsersByStudentIdTests {
        @Test
        @DisplayName("Should find users by student ID")
        void testGetUsersByStudentIdSuccess() {
            List<Users> users = List.of(testUser);
            when(userRepository.findByStudentIdContainingIgnoreCase("SV001")).thenReturn(users);
            
            List<Users> result = userService.getUsersByStudentId("SV001");
            
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("SV001", result.get(0).getStudentId());
            verify(userRepository).findByStudentIdContainingIgnoreCase("SV001");
        }

        @Test
        @DisplayName("Should handle case-insensitive search")
        void testGetUsersByStudentIdCaseInsensitive() {
            List<Users> users = List.of(testUser);
            when(userRepository.findByStudentIdContainingIgnoreCase("sv001")).thenReturn(users);
            
            List<Users> result = userService.getUsersByStudentId("sv001");
            
            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should return multiple users with similar student IDs")
        void testGetUsersByStudentIdMultiple() {
            testUser2.setStudentId("SV001B");
            List<Users> users = List.of(testUser, testUser2);
            when(userRepository.findByStudentIdContainingIgnoreCase("SV001")).thenReturn(users);
            
            List<Users> result = userService.getUsersByStudentId("SV001");
            
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should return empty list when no matches found")
        void testGetUsersByStudentIdNotFound() {
            when(userRepository.findByStudentIdContainingIgnoreCase("INVALID")).thenReturn(List.of());
            
            List<Users> result = userService.getUsersByStudentId("INVALID");
            
            assertTrue(result.isEmpty());
        }
    }

    // ============ UPDATE USER TAG TESTS ============
    @Nested
    @DisplayName("Update User Tag Tests")
    class UpdateUserTagTests {
        @Test
        @DisplayName("Should add tags to user successfully")
        void testUpdateUserTagSuccess() {
            List<Tags> tags = List.of(testTag);
            when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
            when(tagRepository.findById("tag123")).thenReturn(Optional.of(testTag));
            when(userRepository.save(testUser)).thenReturn(testUser);
            
            assertDoesNotThrow(() -> userService.updateUserTag("user123", tags));
            verify(tagRepository).findById("tag123");
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void testUpdateUserTagUserNotFound() {
            List<Tags> tags = List.of(testTag);
            when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());
            
            AppException exception = assertThrows(AppException.class,
                () -> userService.updateUserTag("nonexistent", tags));
            assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        @DisplayName("Should throw exception when tag not found")
        void testUpdateUserTagNotFound() {
            List<Tags> tags = List.of(testTag);
            when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
            when(tagRepository.findById("tag123")).thenReturn(Optional.empty());
            
            AppException exception = assertThrows(AppException.class,
                () -> userService.updateUserTag("user123", tags));
            assertEquals(ErrorCode.TAG_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        @DisplayName("Should clear existing tags and add new ones")
        void testUpdateUserTagClearAndAdd() {
            Tags oldTag = new Tags();
            oldTag.setTagId("oldtag");
            UserTag oldUserTag = new UserTag();
            testUser.getUserTags().add(oldUserTag);

            List<Tags> newTags = List.of(testTag);
            when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
            when(tagRepository.findById("tag123")).thenReturn(Optional.of(testTag));
            when(userRepository.save(testUser)).thenReturn(testUser);
            
            assertDoesNotThrow(() -> userService.updateUserTag("user123", newTags));
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Should handle empty tag list")
        void testUpdateUserTagEmptyList() {
            when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
            when(userRepository.save(testUser)).thenReturn(testUser);
            
            assertDoesNotThrow(() -> userService.updateUserTag("user123", List.of()));
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Should handle multiple tags")
        void testUpdateUserTagMultipleTags() {
            Tags tag2 = new Tags();
            tag2.setTagId("tag456");
            List<Tags> tags = List.of(testTag, tag2);

            when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
            when(tagRepository.findById("tag123")).thenReturn(Optional.of(testTag));
            when(tagRepository.findById("tag456")).thenReturn(Optional.of(tag2));
            when(userRepository.save(testUser)).thenReturn(testUser);
            
            assertDoesNotThrow(() -> userService.updateUserTag("user123", tags));
            verify(tagRepository, times(2)).findById(anyString());
            verify(userRepository).save(testUser);
        }
    }
}
