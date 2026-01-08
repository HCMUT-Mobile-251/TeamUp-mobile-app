package com.teamup.main.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.teamup.main.dto.request.TagRequest;
import com.teamup.main.dto.response.TagResponse;
import com.teamup.main.exception.AppException;
import com.teamup.main.enums.ErrorCode;
import com.teamup.main.model.Tags;
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
        List<Tags> byName = tagRepository.findByNameContainingIgnoreCase(tag);
        return byName;
    }

    public List<TagResponse> getIndividualTags(String userId) {
        // lấy tag của user
        Set<String> userTagIds = userService.findById(userId)
                .getUserTags()
                .stream()
                .map(userTag -> userTag.getTag().getTagId())
                .collect(Collectors.toSet());

        List<TagResponse> result = new ArrayList<>();

        // nếu user chưa có tag thì trả về random 10 tag với isUserTag = false
        if (userTagIds.isEmpty()) {
            List<Tags> randomTags = tagRepository.findRandomTags(PageRequest.of(0, 10));

            for (Tags tag : randomTags) {
                result.add(TagResponse.builder()
                        .tagId(tag.getTagId())
                        .name(tag.getName())
                        .isUserTag(false)
                        .build());
            }
            return result;
        }

        // Lấy tất cả tags để hiển thị, đánh dấu tag nào là của user
        List<Tags> allTags = tagRepository.findAll();
        for (Tags tag : allTags) {
            result.add(TagResponse.builder()
                    .tagId(tag.getTagId())
                    .name(tag.getName())
                    .isUserTag(userTagIds.contains(tag.getTagId()))
                    .build());
        }

        return result;
    }

    public Tags createTagByUser(TagRequest request) {
        // Kiểm tra xem tag đã tồn tại chưa (case-insensitive)
        List<Tags> existingTags = tagRepository.findByNameContainingIgnoreCase(request.getName());

        // Nếu đã có tag trùng tên chính xác thì trả về tag đó
        for (Tags existingTag : existingTags) {
            if (existingTag.getName().equalsIgnoreCase(request.getName())) {
                return existingTag;
            }
        }

        // Nếu chưa có thì tạo mới
        Tags newTag = new Tags();
        newTag.setName(request.getName());
        return tagRepository.save(newTag);
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
