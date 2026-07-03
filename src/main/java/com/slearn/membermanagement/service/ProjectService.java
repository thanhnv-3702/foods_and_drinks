package com.slearn.membermanagement.service;

import com.slearn.membermanagement.dto.ProjectForm;
import com.slearn.membermanagement.entity.NotificationAction;
import com.slearn.membermanagement.entity.Project;
import com.slearn.membermanagement.entity.ProjectMember;
import com.slearn.membermanagement.entity.Team;
import com.slearn.membermanagement.entity.User;
import com.slearn.membermanagement.repository.ProjectMemberRepository;
import com.slearn.membermanagement.repository.ProjectRepository;
import com.slearn.membermanagement.repository.TeamRepository;
import com.slearn.membermanagement.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;
    private final NotificationService notificationService;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectMemberRepository projectMemberRepository,
                          TeamRepository teamRepository,
                          UserRepository userRepository,
                          ActivityLogService activityLogService,
                          NotificationService notificationService) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public Page<Project> findAll(Long teamId, Pageable pageable) {
        if (teamId != null) {
            return projectRepository.findByTeamId(teamId, pageable);
        }
        return projectRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Map<Long, Long> memberCountByProject() {
        Map<Long, Long> counts = new HashMap<>();
        for (Object[] row : projectMemberRepository.countGroupedByProject()) {
            counts.put((Long) row[0], (Long) row[1]);
        }
        return counts;
    }

    @Transactional(readOnly = true)
    public List<Team> findAllTeams() {
        return teamRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Project getById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy dự án id=" + id));
    }

    @Transactional(readOnly = true)
    public ProjectForm getFormById(Long id) {
        Project p = getById(id);
        List<Long> memberIds = projectMemberRepository.findByProjectId(id).stream()
                .map(pm -> pm.getUser().getId())
                .collect(Collectors.toList());
        return ProjectForm.builder()
                .id(p.getId())
                .name(p.getName())
                .abbreviation(p.getAbbreviation())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .teamId(p.getTeam() != null ? p.getTeam().getId() : null)
                .leaderId(p.getLeader() != null ? p.getLeader().getId() : null)
                .memberIds(memberIds)
                .build();
    }

    @Transactional
    public Project create(ProjectForm form) {
        Project project = Project.builder()
                .name(form.getName())
                .abbreviation(form.getAbbreviation())
                .startDate(form.getStartDate())
                .endDate(form.getEndDate())
                .team(resolveTeam(form.getTeamId()))
                .leader(resolveUser(form.getLeaderId()))
                .build();
        project = projectRepository.save(project);
        syncMembers(project, form.getMemberIds());
        notificationService.notifyProjectCrud(project, NotificationAction.CREATE);
        activityLogService.record("CREATE_PROJECT",
                "Tạo dự án '" + project.getName() + "' (id=" + project.getId() + ")");
        return project;
    }

    @Transactional
    public Project update(Long id, ProjectForm form) {
        Project project = getById(id);
        project.setName(form.getName());
        project.setAbbreviation(form.getAbbreviation());
        project.setStartDate(form.getStartDate());
        project.setEndDate(form.getEndDate());
        project.setTeam(resolveTeam(form.getTeamId()));
        project.setLeader(resolveUser(form.getLeaderId()));
        projectRepository.save(project);
        syncMembers(project, form.getMemberIds());
        notificationService.notifyProjectCrud(project, NotificationAction.UPDATE);
        activityLogService.record("UPDATE_PROJECT",
                "Cập nhật dự án '" + project.getName() + "' (id=" + project.getId() + ")");
        return project;
    }

    /**
     * Xóa project: gỡ toàn bộ thành viên (project_members) rồi xóa project.
     */
    @Transactional
    public void delete(Long id) {
        Project project = getById(id);
        notificationService.notifyProjectCrud(project, NotificationAction.DELETE);
        projectMemberRepository.deleteByProjectId(id);
        projectRepository.delete(project);
        activityLogService.record("DELETE_PROJECT",
                "Xóa dự án '" + project.getName() + "' (id=" + id + ")");
    }

    /**
     * Đồng bộ danh sách thành viên: thêm member mới, gỡ member không còn được chọn.
     */
    private void syncMembers(Project project, List<Long> desiredUserIds) {
        Set<Long> desired = desiredUserIds == null ? new HashSet<>() : new HashSet<>(desiredUserIds);
        List<ProjectMember> existing = projectMemberRepository.findByProjectId(project.getId());
        Set<Long> existingUserIds = new HashSet<>();

        for (ProjectMember pm : existing) {
            Long uid = pm.getUser().getId();
            existingUserIds.add(uid);
            if (!desired.contains(uid)) {
                projectMemberRepository.delete(pm);
            }
        }

        List<ProjectMember> toAdd = new ArrayList<>();
        for (Long uid : desired) {
            if (!existingUserIds.contains(uid)) {
                toAdd.add(ProjectMember.builder()
                        .project(project)
                        .user(resolveUser(uid))
                        .build());
            }
        }
        if (!toAdd.isEmpty()) {
            projectMemberRepository.saveAll(toAdd);
        }
    }

    private Team resolveTeam(Long teamId) {
        if (teamId == null) {
            return null;
        }
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy team id=" + teamId));
    }

    private User resolveUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng id=" + userId));
    }
}
