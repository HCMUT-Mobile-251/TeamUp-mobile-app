package com.teamup.main.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamup.main.exception.AppException;
import com.teamup.main.enums.ErrorCode;
import com.teamup.main.model.Tags;
import com.teamup.main.model.UserTag;
import com.teamup.main.repository.TagRepository;

@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserService userService;

    /*
     * User only
     */
    public List<Tags> getTagByName(String tag) {
        List<Tags> byName = tagRepository.findByTagNameContainingIgnoreCase(tag);
        return byName;
    }

    public List<Tags> getIndividualTags(String userId) {
        // lấy tag của user
        Set<Tags> tags = userService.findById(userId)
                .getUserTags()
                .stream()
                .map(UserTag::getTag)
                .collect(Collectors.toSet());

        // nếu user chưa có tag thì add random 10 tag
        if (tags.isEmpty()) {
            return tagRepository.findRandomTags(10);
        }

        return new java.util.ArrayList<>(tags);
    }

    /*
     * Admin only
     */
    public List<Tags> createTag(List<Tags> tags) {
        return tagRepository.saveAll(tags);
    }

    public List<Tags> getTags() {
        return tagRepository.findAll();
    }

    public Tags findTag(String tagId) {
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new AppException(ErrorCode.TAG_NOT_FOUND));
    }

    public void deleteTag(String tagId) {
        findTag(tagId);
        tagRepository.deleteById(tagId);
    }
}
