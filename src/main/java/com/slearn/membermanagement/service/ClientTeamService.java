package com.slearn.membermanagement.service;

import com.slearn.membermanagement.entity.Project;
import com.slearn.membermanagement.entity.Team;
import com.slearn.membermanagement.entity.User;
import com.slearn.membermanagement.repository.ProjectRepository;
import com.slearn.membermanagement.repository.TeamRepository;
import com.slearn.membermanagement.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClientTeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public ClientTeamService(TeamRepository teamRepository,
                             UserRepository userRepository,
                             ProjectRepository projectRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public Page<Team> findAll(Pageable pageable) {
        return teamRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Map<Long, Long> memberCountByTeam() {
        Map<Long, Long> counts = new HashMap<>();
        for (Object[] row : userRepository.countGroupedByTeam()) {
            counts.put((Long) row[0], (Long) row[1]);
        }
        return counts;
    }

    @Transactional(readOnly = true)
    public Team getTeam(Long id) {
        return teamRepository.findWithLeaderById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy team id=" + id));
    }

    @Transactional(readOnly = true)
    public List<User> getMembers(Long teamId) {
        return userRepository.findByTeamIdOrderByNameAsc(teamId);
    }

    @Transactional(readOnly = true)
    public List<Project> getProjects(Long teamId) {
        return projectRepository.findByTeamId(teamId);
    }
}
