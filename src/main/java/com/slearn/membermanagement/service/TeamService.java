package com.slearn.membermanagement.service;

import com.slearn.membermanagement.dto.TeamForm;
import com.slearn.membermanagement.entity.NotificationAction;
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
    private final ActivityLogService activityLogService;
    private final NotificationService notificationService;
    private final MessageService messages;

    public TeamService(TeamRepository teamRepository, UserRepository userRepository,
                       ActivityLogService activityLogService,
                       NotificationService notificationService,
                       MessageService messages) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
        this.notificationService = notificationService;
        this.messages = messages;
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
                .orElseThrow(() -> new EntityNotFoundException(messages.get("error.team.notFound", id)));
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
        teamRepository.save(team);
        notificationService.notifyTeamCrud(team, NotificationAction.CREATE);
        activityLogService.record("CREATE_TEAM",
                messages.get("activity.team.created", team.getName(), team.getId()));
        return team;
    }

    @Transactional
    public Team update(Long id, TeamForm form) {
        Team team = getById(id);
        team.setName(form.getName());
        team.setDescription(form.getDescription());
        team.setLeader(resolveLeader(form.getLeaderId()));
        teamRepository.save(team);
        notificationService.notifyTeamCrud(team, NotificationAction.UPDATE);
        activityLogService.record("UPDATE_TEAM",
                messages.get("activity.team.updated", team.getName(), team.getId()));
        return team;
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
            return messages.get("error.team.delete.hasMembers", team.getName(), memberCount);
        }
        notificationService.notifyTeamCrud(team, NotificationAction.DELETE);
        teamRepository.delete(team);
        activityLogService.record("DELETE_TEAM",
                messages.get("activity.team.deleted", team.getName(), id));
        return null;
    }

    private User resolveLeader(Long leaderId) {
        if (leaderId == null) {
            return null;
        }
        return userRepository.findById(leaderId)
                .orElseThrow(() -> new EntityNotFoundException(messages.get("error.user.notFound", leaderId)));
    }
}
