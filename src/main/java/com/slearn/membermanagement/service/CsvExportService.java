package com.slearn.membermanagement.service;

import com.slearn.membermanagement.entity.ActivityLog;
import com.slearn.membermanagement.entity.Position;
import com.slearn.membermanagement.entity.Project;
import com.slearn.membermanagement.entity.ProjectMember;
import com.slearn.membermanagement.entity.Skill;
import com.slearn.membermanagement.entity.Team;
import com.slearn.membermanagement.entity.User;
import com.slearn.membermanagement.repository.ActivityLogRepository;
import com.slearn.membermanagement.repository.PositionRepository;
import com.slearn.membermanagement.repository.ProjectMemberRepository;
import com.slearn.membermanagement.repository.ProjectRepository;
import com.slearn.membermanagement.repository.SkillRepository;
import com.slearn.membermanagement.repository.TeamRepository;
import com.slearn.membermanagement.repository.UserRepository;
import com.slearn.membermanagement.util.CsvUtil;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CsvExportService {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Sort BY_ID = Sort.by("id").ascending();

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final PositionRepository positionRepository;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ActivityLogRepository activityLogRepository;
    private final MessageService messages;

    public CsvExportService(UserRepository userRepository,
                            SkillRepository skillRepository,
                            PositionRepository positionRepository,
                            TeamRepository teamRepository,
                            ProjectRepository projectRepository,
                            ProjectMemberRepository projectMemberRepository,
                            ActivityLogRepository activityLogRepository,
                            MessageService messages) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.positionRepository = positionRepository;
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.activityLogRepository = activityLogRepository;
        this.messages = messages;
    }

    @Transactional(readOnly = true)
    public String exportUsers() {
        Map<Long, List<String>> skillsByUser = skillRepository.findAll().stream()
                .collect(Collectors.groupingBy(s -> s.getUser().getId(),
                        Collectors.mapping(CsvExportService::skillLabel, Collectors.toList())));

        List<List<String>> rows = new ArrayList<>();
        for (User u : userRepository.findAll(BY_ID)) {
            rows.add(List.of(
                    str(u.getId()),
                    nz(u.getName()),
                    nz(u.getEmail()),
                    fmt(u.getBirthday()),
                    u.getRole() != null ? u.getRole().name() : "",
                    u.getTeam() != null ? nz(u.getTeam().getName()) : "",
                    u.getPosition() != null ? nz(u.getPosition().getName()) : "",
                    String.join("; ", skillsByUser.getOrDefault(u.getId(), List.of()))
            ));
        }
        return CsvUtil.build(
                List.of(
                        messages.get("csv.header.id"),
                        messages.get("csv.header.name"),
                        messages.get("csv.header.email"),
                        messages.get("csv.header.birthday"),
                        messages.get("csv.header.role"),
                        messages.get("csv.header.team"),
                        messages.get("csv.header.position"),
                        messages.get("csv.header.skills")
                ),
                rows);
    }

    @Transactional(readOnly = true)
    public String exportPositions() {
        List<List<String>> rows = new ArrayList<>();
        for (Position p : positionRepository.findAll(BY_ID)) {
            rows.add(List.of(str(p.getId()), nz(p.getName()), nz(p.getAbbreviation())));
        }
        return CsvUtil.build(List.of(
                messages.get("csv.header.id"),
                messages.get("csv.header.name"),
                messages.get("csv.header.abbreviation")
        ), rows);
    }

    @Transactional(readOnly = true)
    public String exportSkills() {
        List<List<String>> rows = new ArrayList<>();
        for (Skill s : skillRepository.findAll(BY_ID)) {
            rows.add(List.of(
                    str(s.getId()),
                    nz(s.getName()),
                    nz(s.getLevel()),
                    str(s.getUsedYearNumber()),
                    s.getUser() != null ? nz(s.getUser().getName()) : ""
            ));
        }
        return CsvUtil.build(List.of(
                messages.get("csv.header.id"),
                messages.get("csv.header.name"),
                messages.get("csv.header.level"),
                messages.get("csv.header.usedYears"),
                messages.get("csv.header.user")
        ), rows);
    }

    @Transactional(readOnly = true)
    public String exportTeams() {
        List<List<String>> rows = new ArrayList<>();
        for (Team t : teamRepository.findAll(BY_ID)) {
            rows.add(List.of(
                    str(t.getId()),
                    nz(t.getName()),
                    nz(t.getDescription()),
                    t.getLeader() != null ? nz(t.getLeader().getName()) : "",
                    str(userRepository.countByTeamId(t.getId()))
            ));
        }
        return CsvUtil.build(List.of(
                messages.get("csv.header.id"),
                messages.get("csv.header.name"),
                messages.get("csv.header.description"),
                messages.get("csv.header.leader"),
                messages.get("csv.header.memberCount")
        ), rows);
    }

    @Transactional(readOnly = true)
    public String exportProjects() {
        Map<Long, List<String>> membersByProject = projectMemberRepository.findAll().stream()
                .collect(Collectors.groupingBy(pm -> pm.getProject().getId(),
                        Collectors.mapping(pm -> nz(pm.getUser().getName()), Collectors.toList())));

        List<List<String>> rows = new ArrayList<>();
        for (Project p : projectRepository.findAll(BY_ID)) {
            rows.add(List.of(
                    str(p.getId()),
                    nz(p.getName()),
                    nz(p.getAbbreviation()),
                    fmt(p.getStartDate()),
                    fmt(p.getEndDate()),
                    p.getTeam() != null ? nz(p.getTeam().getName()) : "",
                    p.getLeader() != null ? nz(p.getLeader().getName()) : "",
                    String.join("; ", membersByProject.getOrDefault(p.getId(), List.of()))
            ));
        }
        return CsvUtil.build(
                List.of(
                        messages.get("csv.header.id"),
                        messages.get("csv.header.name"),
                        messages.get("csv.header.abbreviation"),
                        messages.get("csv.header.startDate"),
                        messages.get("csv.header.endDate"),
                        messages.get("csv.header.team"),
                        messages.get("csv.header.leader"),
                        messages.get("csv.header.members")
                ),
                rows);
    }

    @Transactional(readOnly = true)
    public String exportActivityLogs() {
        List<List<String>> rows = new ArrayList<>();
        for (ActivityLog log : activityLogRepository.findAll(Sort.by("createdAt").descending())) {
            rows.add(List.of(
                    str(log.getId()),
                    fmtDateTime(log.getCreatedAt()),
                    nz(log.getAction()),
                    nz(log.getDescription()),
                    log.getUser() != null ? nz(log.getUser().getName()) : messages.get("notification.actor.system")
            ));
        }
        return CsvUtil.build(List.of(
                messages.get("csv.header.id"),
                messages.get("csv.header.time"),
                messages.get("csv.header.action"),
                messages.get("csv.header.description"),
                messages.get("csv.header.user")
        ), rows);
    }

    private static String skillLabel(Skill s) {
        StringBuilder sb = new StringBuilder(s.getName() == null ? "" : s.getName());
        boolean hasLevel = s.getLevel() != null && !s.getLevel().isBlank();
        boolean hasYears = s.getUsedYearNumber() != null;
        if (hasLevel || hasYears) {
            sb.append(" (");
            if (hasLevel) {
                sb.append(s.getLevel());
            }
            if (hasYears) {
                sb.append(hasLevel ? ", " : "").append(s.getUsedYearNumber()).append("y");
            }
            sb.append(")");
        }
        return sb.toString();
    }

    private static String fmt(LocalDate d) {
        return d != null ? d.format(DATE) : "";
    }

    private static String fmtDateTime(LocalDateTime dt) {
        return dt != null ? dt.format(DATETIME) : "";
    }

    private static String str(Object o) {
        return o != null ? o.toString() : "";
    }

    private static String nz(String s) {
        return s != null ? s : "";
    }
}
