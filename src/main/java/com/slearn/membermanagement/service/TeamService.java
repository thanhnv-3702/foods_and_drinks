package com.slearn.membermanagement.service;

import com.slearn.membermanagement.dto.TeamForm;
import com.slearn.membermanagement.entity.Team;
import com.slearn.membermanagement.entity.User;
import com.slearn.membermanagement.repository.TeamRepository;
import com.slearn.membermanagement.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public TeamService(TeamRepository teamRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<Team> findAll(Pageable pageable) {
        return teamRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Team getById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy team id=" + id));
    }

    @Transactional(readOnly = true)
    public TeamForm getFormById(Long id) {
        Team t = getById(id);
        return TeamForm.builder()
                .id(t.getId())
                .name(t.getName())
                .description(t.getDescription())
                .leaderId(t.getLeader() != null ? t.getLeader().getId() : null)
                .build();
    }

    @Transactional
    public Team create(TeamForm form) {
        Team team = Team.builder()
                .name(form.getName())
                .description(form.getDescription())
                .leader(resolveLeader(form.getLeaderId()))
                .build();
        return teamRepository.save(team);
    }

    @Transactional
    public Team update(Long id, TeamForm form) {
        Team team = getById(id);
        team.setName(form.getName());
        team.setDescription(form.getDescription());
        team.setLeader(resolveLeader(form.getLeaderId()));
        return teamRepository.save(team);
    }

    /**
     * Xóa team. Không cho xóa nếu team còn thành viên.
     * @return null nếu xóa thành công, hoặc thông báo lỗi ràng buộc.
     */
    @Transactional
    public String delete(Long id) {
        Team team = getById(id);
        long memberCount = userRepository.countByTeamId(id);
        if (memberCount > 0) {
            return "Không thể xóa team \"" + team.getName() + "\" vì còn " + memberCount + " thành viên.";
        }
        teamRepository.delete(team);
        return null;
    }

    private User resolveLeader(Long leaderId) {
        if (leaderId == null) {
            return null;
        }
        return userRepository.findById(leaderId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng id=" + leaderId));
    }
}
