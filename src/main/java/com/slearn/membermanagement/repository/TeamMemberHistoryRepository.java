package com.slearn.membermanagement.repository;

import com.slearn.membermanagement.entity.TeamMemberHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamMemberHistoryRepository extends JpaRepository<TeamMemberHistory, Long> {

    List<TeamMemberHistory> findByTeamIdOrderByJoinedAtDesc(Long teamId);

    List<TeamMemberHistory> findByUserIdOrderByJoinedAtDesc(Long userId);
}
