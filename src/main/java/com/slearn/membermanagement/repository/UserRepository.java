package com.slearn.membermanagement.repository;

import com.slearn.membermanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByTeamId(Long teamId);

    List<User> findByTeamIdOrderByNameAsc(Long teamId);

    @Query("select u from User u left join fetch u.team where u.team is null or u.team.id <> :teamId order by u.name asc")
    List<User> findCandidatesForTeam(@Param("teamId") Long teamId);
}
