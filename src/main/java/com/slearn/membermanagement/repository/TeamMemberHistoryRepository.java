package com.slearn.membermanagement.repository;

import com.slearn.membermanagement.entity.TeamMemberHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberHistoryRepository extends JpaRepository<TeamMemberHistory, Long> {

    @EntityGraph(attributePaths = {"user"})
    List<TeamMemberHistory> findByTeamIdOrderByJoinedAtDesc(Long teamId);

    List<TeamMemberHistory> findByUserIdOrderByJoinedAtDesc(Long userId);

    Optional<TeamMemberHistory> findFirstByUserIdAndLeftAtIsNull(Long userId);
}
