package com.slearn.membermanagement.repository;

import com.slearn.membermanagement.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Long> {

    Optional<Position> findFirstByNameIgnoreCase(String name);
}
