package com.slearn.membermanagement.repository;

import com.slearn.membermanagement.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Override
    @EntityGraph(attributePaths = "leader")
    Page<Team> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "leader")
    Optional<Team> findWithLeaderById(Long id);

    Optional<Team> findFirstByNameIgnoreCase(String name);
}
