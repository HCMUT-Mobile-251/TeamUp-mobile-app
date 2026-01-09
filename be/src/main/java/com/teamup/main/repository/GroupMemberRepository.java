package com.teamup.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamup.main.enums.GroupStatus;
import com.teamup.main.model.GroupMember;
import com.teamup.main.model.PairId;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, PairId> {
  // Custom query methods can be defined here if needed

  // tìm kiếm theo tên nhóm
  Page<GroupMember> findById_FirstIdAndGroup_NameContainingIgnoreCase(String userId, String name, Pageable pageable);

  // tìm kiếm trong mối liên hệ giữa user và group, giảm dần theo utc time
  Page<GroupMember> findById_FirstIdAndIsDeletedOrderByTimeDesc(String userId, boolean isDeleted, Pageable pageable);

  // nếu là chủ nhóm, được nhận trạng thái WAITING_APPROVAL
  Page<GroupMember> findById_SecondIdAndStatusAndIsDeletedOrderByTimeDesc(String groupId, GroupStatus status, boolean isDeleted, Pageable pageable);

  // tìm thông báo theo pairId và trạng thái khác JOINED
  GroupMember findByIdAndStatusNot(PairId id, GroupStatus status);
}