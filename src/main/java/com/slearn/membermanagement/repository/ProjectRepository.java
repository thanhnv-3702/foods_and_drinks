package com.slearn.membermanagement.repository;

import com.slearn.membermanagement.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByTeamId(Long teamId);

    @Override
    @EntityGraph(attributePaths = {"team", "leader"})
    Page<Project> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"team", "leader"})
    Page<Project> findByTeamId(Long teamId, Pageable pageable);
}
