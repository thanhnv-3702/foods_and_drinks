package com.slearn.membermanagement.repository;

import com.slearn.membermanagement.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
