package com.slearn.membermanagement.repository;

import com.slearn.membermanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    @Override
    @EntityGraph(attributePaths = {"team", "position"})
    Page<User> findAll(Pageable pageable);

    long countByTeamId(Long teamId);

    @EntityGraph(attributePaths = {"position"})
    List<User> findByTeamIdOrderByNameAsc(Long teamId);

    @Query("select u.team.id, count(u) from User u where u.team is not null group by u.team.id")
    List<Object[]> countGroupedByTeam();

    @Query("select u from User u left join fetch u.team where u.team is null or u.team.id <> :teamId order by u.name asc")
    List<User> findCandidatesForTeam(@Param("teamId") Long teamId);
}
