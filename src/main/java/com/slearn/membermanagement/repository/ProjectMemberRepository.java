package com.slearn.membermanagement.repository;

import com.slearn.membermanagement.entity.ProjectMember;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    @EntityGraph(attributePaths = {"user"})
    List<ProjectMember> findByProjectId(Long projectId);

    @EntityGraph(attributePaths = {"project", "project.team", "project.leader"})
    List<ProjectMember> findByUserId(Long userId);

    void deleteByProjectId(Long projectId);

    @Query("select pm.project.id, count(pm) from ProjectMember pm group by pm.project.id")
    List<Object[]> countGroupedByProject();
}
