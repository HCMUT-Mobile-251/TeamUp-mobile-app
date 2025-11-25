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
  Page<GroupMember> findById_FirstIdAndGroup_NameContainingIgnoreCase(String userId, String name, Pageable pageable);

  // giảm dần utc time
  Page<GroupMember> findById_FirstIdAndIsDeletedOrderByTimeDesc(String userId, boolean isDeleted, Pageable pageable);

  GroupMember findByIdAndStatusNot(PairId id, GroupStatus status);
}