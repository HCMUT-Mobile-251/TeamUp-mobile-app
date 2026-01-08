package com.teamup.main.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.teamup.main.exception.AppException;
import com.teamup.main.enums.ErrorCode;
import com.teamup.main.enums.GroupStatus;
import com.teamup.main.model.GroupMember;
import com.teamup.main.model.Groups;
import com.teamup.main.model.PairId;
import com.teamup.main.repository.GroupMemberRepository;
import com.teamup.main.repository.GroupRepository;

@Service
public class NotificationService {
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    @Autowired
    private GroupRepository groupRepository;

    /*
     * User only
     */
    public Page<GroupMember> getNotificationByUserId(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<GroupMember> result = new ArrayList<>();
        
        // notifi của các group do user tạo
        List<Groups> leaderGroups = groupRepository.findByLeaderId_UserId(userId);
        for (Groups group : leaderGroups) {
            Page<GroupMember> pendingApprovals = groupMemberRepository
                    .findById_SecondIdAndStatusAndIsDeletedOrderByTimeDesc(
                            group.getGroupId(),
                            GroupStatus.WAITING_APPROVAL,
                            false,
                            pageable);

            result.addAll(pendingApprovals.getContent());
        }

        // notifi của user
        result.addAll(groupMemberRepository
                .findById_FirstIdAndIsDeletedOrderByTimeDesc(
                        userId,
                        false,
                        pageable).getContent());
        result.sort(
                Comparator.comparing(GroupMember::getTime).reversed());

        int start = Math.min((int) pageable.getOffset(), result.size());
        int end = Math.min(start + pageable.getPageSize(), result.size());

        List<GroupMember> pageContent = result.subList(start, end);

        return new PageImpl<>(pageContent, pageable, result.size());
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
        // check nếu là joined thì set isDeleted = true
        GroupMember gm = groupMemberRepository.findById(pairId).orElse(null);
        if (gm.getStatus() == GroupStatus.JOINED) {
            gm.setDeleted(true);
            groupMemberRepository.save(gm);
            return;
        }

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
