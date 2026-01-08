package com.teamup.main.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    TagRepository tagRepository;

    /*
     * User only
     */
    public List<Users> getUsersByStudentId(String studentId) {
        return userRepository.findByStudentIdContainingIgnoreCase(studentId);
    }

    public Users updateUser(String userId, UserUpdateRequest request) {
        Users user = findById(userId);
        userMapper.updateUser(user, request);
        return userRepository.save(user);
    }

    public void deleteUser(String userId) {
        findById(userId);
        userRepository.deleteById(userId);
    }

    public void updateUserTag(String userId, List<Tags> listTag) {
        System.out.println("=== Updating user tags ===");
        System.out.println("User ID: " + userId);
        System.out.println("Number of tags received: " + (listTag != null ? listTag.size() : 0));
        if (listTag != null) {
            listTag.forEach(tag -> System.out.println("Tag: " + tag.getTagId() + " - " + tag.getName()));
        }

        Users user = findById(userId);

        user.getUserTags().clear();
        for (Tags tag : listTag) {
            // check tag exist
            Tags existingTag = tagRepository.findById(tag.getTagId())
                .orElseThrow(() -> new AppException(ErrorCode.TAG_NOT_FOUND));
            PairId id = new PairId(userId, existingTag.getTagId());
            UserTag userTag = new UserTag(id, user, existingTag);
            user.getUserTags().add(userTag);
        }
        userRepository.save(user);
        System.out.println("User tags updated successfully. Total: " + user.getUserTags().size());
    }

    /**
     * Admin only
     */
    public Users createUser(GoogleAccount request) {
        Optional<Users> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isPresent()) {
            return userOpt.get();
        }

        Users user = userMapper.toUser(request);
        return userRepository.save(user);
    }

    public List<Users> getUsers() {
        return userRepository.findAll();
    }

    public Users findById(String userId) {
        return userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public List<Users> findAllById(List<String> listUserId) {
        return userRepository.findAllById(listUserId);
    }
}
