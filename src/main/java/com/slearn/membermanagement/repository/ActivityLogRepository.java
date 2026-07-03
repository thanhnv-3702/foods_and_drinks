package com.slearn.membermanagement.repository;

import com.slearn.membermanagement.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    @Override
    @EntityGraph(attributePaths = "user")
    Page<ActivityLog> findAll(Pageable pageable);
}
