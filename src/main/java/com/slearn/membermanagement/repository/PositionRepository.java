package com.slearn.membermanagement.repository;

import com.slearn.membermanagement.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {
}
