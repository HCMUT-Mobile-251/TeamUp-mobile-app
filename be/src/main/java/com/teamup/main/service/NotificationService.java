package com.teamup.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.teamup.main.exception.AppException;
import com.teamup.main.enums.ErrorCode;
import com.teamup.main.enums.GroupStatus;
import com.teamup.main.model.GroupMember;
import com.teamup.main.model.PairId;
import com.teamup.main.repository.GroupMemberRepository;

@Service
public class NotificationService {
    @Autowired
    private GroupMemberRepository groupMemberRepository;

    /*
     * User only
     */
    public Page<GroupMember> getNotificationByUserId(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return groupMemberRepository.findById_FirstIdAndIsDeletedOrderByTimeDesc(userId, false, pageable);
    }

    public Page<GroupMember> findNotificationByNameByUserId(String userId, String nameGroup, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (groupMemberRepository.findById_FirstIdAndGroup_NameContainingIgnoreCase(userId, nameGroup, pageable)
                .getContent().isEmpty()) {
            throw new AppException(ErrorCode.NOTI_NOT_FOUND);
        }
        return groupMemberRepository.findById_FirstIdAndGroup_NameContainingIgnoreCase(userId, nameGroup, pageable);
    }

    public GroupMember findNotificationById(PairId pairId) {
        if (groupMemberRepository.findByIdAndStatusNot(pairId, GroupStatus.JOINED) == null) {
            throw new AppException(ErrorCode.NOTI_NOT_FOUND);
        }
        return groupMemberRepository.findByIdAndStatusNot(pairId, GroupStatus.JOINED);
    }

    public void deleteNotification(PairId pairId) {
        // check tồn tại
        if (findNotificationById(pairId) == null) {
            throw new AppException(ErrorCode.NOTI_NOT_FOUND);
        }
        groupMemberRepository.deleteById(pairId);
    }
    /*
     * Admin only
     */
}
