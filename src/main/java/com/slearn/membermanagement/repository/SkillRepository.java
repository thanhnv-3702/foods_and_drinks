package com.slearn.membermanagement.repository;

import com.slearn.membermanagement.entity.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findByUserId(Long userId);

    @Override
    @EntityGraph(attributePaths = "user")
    Page<Skill> findAll(Pageable pageable);
}
