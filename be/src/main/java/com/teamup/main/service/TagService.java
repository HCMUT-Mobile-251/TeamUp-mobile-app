package com.teamup.main.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamup.main.exception.AppException;
import com.teamup.main.exception.ErrorCode;
import com.teamup.main.model.Tags;
import com.teamup.main.repository.TagRepository;

@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;

    /*
     * User only
     */
    public List<Tags> getTagByName(String tag) {
        List<Tags> byName = tagRepository.findByNameContainingIgnoreCase(tag);
        return byName;
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
