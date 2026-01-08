package com.teamup.main.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.teamup.main.enums.ErrorCode;
import com.teamup.main.exception.AppException;
import com.teamup.main.model.Tags;
import com.teamup.main.model.UserTag;
import com.teamup.main.model.Users;
import com.teamup.main.repository.TagRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("TagService Tests - Phase 6")
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TagService tagService;

    private Tags testTag;
    private Users testUser;
    private List<Tags> testTagList;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testTag = new Tags("tag1", "Java");

        testUser = new Users();
        testUser.setUserId("user1");
        testUser.setFullName("John Doe");

        testTagList = new ArrayList<>();
        testTagList.add(testTag);
        testTagList.add(new Tags("tag2", "Python"));
        testTagList.add(new Tags("tag3", "JavaScript"));
    }

    @Nested
    @DisplayName("Get Tag By Name Tests")
    class GetTagByNameTests {

        @Test
        @DisplayName("Should retrieve tags by name containing search term")
        void testGetTagByNameSuccess() {
            // Arrange
            String tagName = "Java";
            when(tagRepository.findByTagNameContainingIgnoreCase(tagName)).thenReturn(testTagList);

            // Act
            List<Tags> result = tagService.getTagByName(tagName);

            // Assert
            assertNotNull(result);
            assertEquals(3, result.size());
            assertEquals("Java", result.get(0).getTagName());
            verify(tagRepository, times(1)).findByTagNameContainingIgnoreCase(tagName);
        }

        @Test
        @DisplayName("Should perform case-insensitive search")
        void testGetTagByNameCaseInsensitive() {
            // Arrange
            String tagName = "java";
            when(tagRepository.findByTagNameContainingIgnoreCase(tagName)).thenReturn(testTagList);

            // Act
            List<Tags> result = tagService.getTagByName(tagName);

            // Assert
            assertNotNull(result);
            assertEquals(3, result.size());
            verify(tagRepository, times(1)).findByTagNameContainingIgnoreCase(tagName);
        }

        @Test
        @DisplayName("Should return empty list when no tags match search term")
        void testGetTagByNameNoResults() {
            // Arrange
            String tagName = "NonExistent";
            when(tagRepository.findByTagNameContainingIgnoreCase(tagName)).thenReturn(new ArrayList<>());

            // Act
            List<Tags> result = tagService.getTagByName(tagName);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(tagRepository, times(1)).findByTagNameContainingIgnoreCase(tagName);
        }

        @Test
        @DisplayName("Should handle partial tag name matching")
        void testGetTagByNamePartialMatch() {
            // Arrange
            String tagName = "Script";
            List<Tags> partialMatch = new ArrayList<>();
            partialMatch.add(new Tags("tag3", "JavaScript"));
            when(tagRepository.findByTagNameContainingIgnoreCase(tagName)).thenReturn(partialMatch);

            // Act
            List<Tags> result = tagService.getTagByName(tagName);

            // Assert
            assertEquals(1, result.size());
            assertEquals("JavaScript", result.get(0).getTagName());
        }

        @Test
        @DisplayName("Should handle single character search")
        void testGetTagByNameSingleCharacter() {
            // Arrange
            String tagName = "J";
            List<Tags> matches = new ArrayList<>();
            matches.add(new Tags("tag3", "JavaScript"));
            when(tagRepository.findByTagNameContainingIgnoreCase(tagName)).thenReturn(matches);

            // Act
            List<Tags> result = tagService.getTagByName(tagName);

            // Assert
            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("Get Individual Tags Tests")
    class GetIndividualTagsTests {

        @Test
        @DisplayName("Should retrieve user's tags when user has tags")
        void testGetIndividualTagsWithUserTags() {
            // Arrange
            String userId = "user1";
            UserTag userTag1 = new UserTag();
            userTag1.setTag(new Tags("tag1", "Java"));
            UserTag userTag2 = new UserTag();
            userTag2.setTag(new Tags("tag2", "Python"));

            Set<UserTag> userTags = new HashSet<>();
            userTags.add(userTag1);
            userTags.add(userTag2);

            testUser.setUserTags(userTags);
            when(userService.findById(userId)).thenReturn(testUser);

            // Act
            List<Tags> result = tagService.getIndividualTags(userId);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(result.stream().anyMatch(t -> "Java".equals(t.getTagName())));
            assertTrue(result.stream().anyMatch(t -> "Python".equals(t.getTagName())));
            verify(userService, times(1)).findById(userId);
        }

        @Test
        @DisplayName("Should return random tags when user has no tags")
        void testGetIndividualTagsWithoutUserTags() {
            // Arrange
            String userId = "user1";
            Set<UserTag> emptyTags = new HashSet<>();
            testUser.setUserTags(emptyTags);

            List<Tags> randomTags = new ArrayList<>();
            randomTags.add(new Tags("tag1", "Java"));
            randomTags.add(new Tags("tag2", "Python"));
            randomTags.add(new Tags("tag3", "JavaScript"));

            when(userService.findById(userId)).thenReturn(testUser);
            when(tagRepository.findRandomTags(10)).thenReturn(randomTags);

            // Act
            List<Tags> result = tagService.getIndividualTags(userId);

            // Assert
            assertNotNull(result);
            assertEquals(3, result.size());
            verify(userService, times(1)).findById(userId);
            verify(tagRepository, times(1)).findRandomTags(10);
        }

        @Test
        @DisplayName("Should fetch 10 random tags when user has empty tag set")
        void testGetIndividualTagsRequestsTenRandomTags() {
            // Arrange
            String userId = "user1";
            testUser.setUserTags(new HashSet<>());

            List<Tags> randomTags = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                randomTags.add(new Tags("tag" + i, "Tag" + i));
            }

            when(userService.findById(userId)).thenReturn(testUser);
            when(tagRepository.findRandomTags(10)).thenReturn(randomTags);

            // Act
            List<Tags> result = tagService.getIndividualTags(userId);

            // Assert
            assertEquals(10, result.size());
            verify(tagRepository, times(1)).findRandomTags(10);
        }

        @Test
        @DisplayName("Should handle user with single tag")
        void testGetIndividualTagsWithSingleTag() {
            // Arrange
            String userId = "user1";
            UserTag userTag = new UserTag();
            userTag.setTag(new Tags("tag1", "Java"));

            Set<UserTag> userTags = new HashSet<>();
            userTags.add(userTag);

            testUser.setUserTags(userTags);
            when(userService.findById(userId)).thenReturn(testUser);

            // Act
            List<Tags> result = tagService.getIndividualTags(userId);

            // Assert
            assertEquals(1, result.size());
            assertEquals("Java", result.get(0).getTagName());
        }

        @Test
        @DisplayName("Should preserve all user tags without duplication")
        void testGetIndividualTagsPreserveAllTags() {
            // Arrange
            String userId = "user1";
            UserTag userTag1 = new UserTag();
            userTag1.setTag(new Tags("tag1", "Java"));
            UserTag userTag2 = new UserTag();
            userTag2.setTag(new Tags("tag2", "Python"));
            UserTag userTag3 = new UserTag();
            userTag3.setTag(new Tags("tag3", "JavaScript"));

            Set<UserTag> userTags = new HashSet<>();
            userTags.add(userTag1);
            userTags.add(userTag2);
            userTags.add(userTag3);

            testUser.setUserTags(userTags);
            when(userService.findById(userId)).thenReturn(testUser);

            // Act
            List<Tags> result = tagService.getIndividualTags(userId);

            // Assert
            assertEquals(3, result.size());
            verify(userService, times(1)).findById(userId);
        }
    }

    @Nested
    @DisplayName("Create Tag Tests")
    class CreateTagTests {

        @Test
        @DisplayName("Should create multiple tags successfully")
        void testCreateTagsSuccess() {
            // Arrange
            when(tagRepository.saveAll(testTagList)).thenReturn(testTagList);

            // Act
            List<Tags> result = tagService.createTag(testTagList);

            // Assert
            assertNotNull(result);
            assertEquals(3, result.size());
            verify(tagRepository, times(1)).saveAll(testTagList);
        }

        @Test
        @DisplayName("Should create single tag")
        void testCreateSingleTag() {
            // Arrange
            List<Tags> singleTag = new ArrayList<>();
            singleTag.add(testTag);
            when(tagRepository.saveAll(singleTag)).thenReturn(singleTag);

            // Act
            List<Tags> result = tagService.createTag(singleTag);

            // Assert
            assertEquals(1, result.size());
            assertEquals("Java", result.get(0).getTagName());
        }

        @Test
        @DisplayName("Should handle empty tag list")
        void testCreateTagsEmptyList() {
            // Arrange
            List<Tags> emptyList = new ArrayList<>();
            when(tagRepository.saveAll(emptyList)).thenReturn(emptyList);

            // Act
            List<Tags> result = tagService.createTag(emptyList);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should save tags with correct fields")
        void testCreateTagsPreservesTagData() {
            // Arrange
            when(tagRepository.saveAll(testTagList)).thenReturn(testTagList);

            // Act
            List<Tags> result = tagService.createTag(testTagList);

            // Assert
            assertEquals("tag1", result.get(0).getTagId());
            assertEquals("Java", result.get(0).getTagName());
            assertEquals("tag2", result.get(1).getTagId());
            assertEquals("Python", result.get(1).getTagName());
        }
    }

    @Nested
    @DisplayName("Get All Tags Tests")
    class GetAllTagsTests {

        @Test
        @DisplayName("Should retrieve all tags from repository")
        void testGetTagsSuccess() {
            // Arrange
            when(tagRepository.findAll()).thenReturn(testTagList);

            // Act
            List<Tags> result = tagService.getTags();

            // Assert
            assertNotNull(result);
            assertEquals(3, result.size());
            verify(tagRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should handle empty tag repository")
        void testGetTagsEmpty() {
            // Arrange
            when(tagRepository.findAll()).thenReturn(new ArrayList<>());

            // Act
            List<Tags> result = tagService.getTags();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return all tags without filtering")
        void testGetTagsReturnsAll() {
            // Arrange
            List<Tags> allTags = new ArrayList<>();
            for (int i = 1; i <= 20; i++) {
                allTags.add(new Tags("tag" + i, "Tag" + i));
            }
            when(tagRepository.findAll()).thenReturn(allTags);

            // Act
            List<Tags> result = tagService.getTags();

            // Assert
            assertEquals(20, result.size());
            verify(tagRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Find Tag By ID Tests")
    class FindTagByIdTests {

        @Test
        @DisplayName("Should find tag by ID successfully")
        void testFindTagSuccess() {
            // Arrange
            String tagId = "tag1";
            when(tagRepository.findById(tagId)).thenReturn(Optional.of(testTag));

            // Act
            Tags result = tagService.findTag(tagId);

            // Assert
            assertNotNull(result);
            assertEquals(tagId, result.getTagId());
            assertEquals("Java", result.getTagName());
            verify(tagRepository, times(1)).findById(tagId);
        }

        @Test
        @DisplayName("Should throw exception when tag not found")
        void testFindTagNotFound() {
            // Arrange
            String tagId = "nonexistent";
            when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

            // Act & Assert
            AppException exception = assertThrows(AppException.class, () -> tagService.findTag(tagId));
            assertEquals(ErrorCode.TAG_NOT_FOUND, exception.getErrorCode());
            verify(tagRepository, times(1)).findById(tagId);
        }

        @Test
        @DisplayName("Should handle null tag ID gracefully")
        void testFindTagNullId() {
            // Arrange
            when(tagRepository.findById(null)).thenReturn(Optional.empty());

            // Act & Assert
            AppException exception = assertThrows(AppException.class, () -> tagService.findTag(null));
            assertEquals(ErrorCode.TAG_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        @DisplayName("Should find tag with correct tag name")
        void testFindTagWithCorrectName() {
            // Arrange
            String tagId = "tag2";
            Tags tag2 = new Tags("tag2", "Python");
            when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag2));

            // Act
            Tags result = tagService.findTag(tagId);

            // Assert
            assertEquals("Python", result.getTagName());
        }
    }

    @Nested
    @DisplayName("Delete Tag Tests")
    class DeleteTagTests {

        @Test
        @DisplayName("Should delete tag successfully")
        void testDeleteTagSuccess() {
            // Arrange
            String tagId = "tag1";
            when(tagRepository.findById(tagId)).thenReturn(Optional.of(testTag));

            // Act
            tagService.deleteTag(tagId);

            // Assert
            verify(tagRepository, times(1)).findById(tagId);
            verify(tagRepository, times(1)).deleteById(tagId);
        }

        @Test
        @DisplayName("Should throw exception when trying to delete non-existent tag")
        void testDeleteTagNotFound() {
            // Arrange
            String tagId = "nonexistent";
            when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

            // Act & Assert
            AppException exception = assertThrows(AppException.class, () -> tagService.deleteTag(tagId));
            assertEquals(ErrorCode.TAG_NOT_FOUND, exception.getErrorCode());
            verify(tagRepository, never()).deleteById(tagId);
        }

        @Test
        @DisplayName("Should verify tag exists before deleting")
        void testDeleteTagVerifiesExistence() {
            // Arrange
            String tagId = "tag1";
            when(tagRepository.findById(tagId)).thenReturn(Optional.of(testTag));

            // Act
            tagService.deleteTag(tagId);

            // Assert
            // findById should be called to verify existence
            verify(tagRepository, times(1)).findById(tagId);
        }

        @Test
        @DisplayName("Should delete with correct tag ID")
        void testDeleteTagWithCorrectId() {
            // Arrange
            String tagId = "tag3";
            Tags tag3 = new Tags("tag3", "JavaScript");
            when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag3));

            // Act
            tagService.deleteTag(tagId);

            // Assert
            verify(tagRepository, times(1)).deleteById(tagId);
        }
    }

    @Nested
    @DisplayName("Integration Scenario Tests")
    class IntegrationScenarioTests {

        @Test
        @DisplayName("Should handle complete tag search and retrieval workflow")
        void testCompleteTagSearchWorkflow() {
            // Arrange
            String tagName = "Java";
            when(tagRepository.findByTagNameContainingIgnoreCase(tagName)).thenReturn(testTagList);
            when(tagRepository.findAll()).thenReturn(testTagList);

            // Act
            List<Tags> searchResult = tagService.getTagByName(tagName);
            List<Tags> allTags = tagService.getTags();

            // Assert
            assertEquals(3, searchResult.size());
            assertEquals(3, allTags.size());
        }

        @Test
        @DisplayName("Should handle tag creation and retrieval workflow")
        void testCreateAndRetrieveTagsWorkflow() {
            // Arrange
            when(tagRepository.saveAll(testTagList)).thenReturn(testTagList);
            when(tagRepository.findAll()).thenReturn(testTagList);

            // Act
            List<Tags> created = tagService.createTag(testTagList);
            List<Tags> retrieved = tagService.getTags();

            // Assert
            assertEquals(3, created.size());
            assertEquals(3, retrieved.size());
            verify(tagRepository, times(1)).saveAll(testTagList);
            verify(tagRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should handle user tag retrieval for empty user")
        void testUserTagRetrievalForNewUser() {
            // Arrange
            String userId = "newuser";
            Users newUser = new Users();
            newUser.setUserId(userId);
            newUser.setUserTags(new HashSet<>());

            List<Tags> randomTags = new ArrayList<>();
            randomTags.add(new Tags("tag1", "Java"));
            randomTags.add(new Tags("tag2", "Python"));

            when(userService.findById(userId)).thenReturn(newUser);
            when(tagRepository.findRandomTags(10)).thenReturn(randomTags);

            // Act
            List<Tags> result = tagService.getIndividualTags(userId);

            // Assert
            assertEquals(2, result.size());
            verify(tagRepository, times(1)).findRandomTags(10);
        }

        @Test
        @DisplayName("Should handle finding and deleting specific tag")
        void testFindAndDeleteTagWorkflow() {
            // Arrange
            String tagId = "tag1";
            when(tagRepository.findById(tagId)).thenReturn(Optional.of(testTag));

            // Act
            Tags found = tagService.findTag(tagId);
            tagService.deleteTag(tagId);

            // Assert
            assertNotNull(found);
            assertEquals("Java", found.getTagName());
            verify(tagRepository, times(2)).findById(tagId);
            verify(tagRepository, times(1)).deleteById(tagId);
        }
    }
}
