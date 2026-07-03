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
import com.slearn.membermanagement.support.TestEntityFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CsvExportServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private ActivityLogRepository activityLogRepository;

    @InjectMocks
    private CsvExportService csvExportService;

    @Test
    void exportPositions_includesHeaderAndRow() {
        Position position = TestEntityFactory.position(1L);
        when(positionRepository.findAll(Sort.by("id").ascending())).thenReturn(List.of(position));

        String csv = csvExportService.exportPositions();

        assertThat(csv).startsWith("\uFEFF");
        assertThat(csv).contains("ID,Name,Abbreviation");
        assertThat(csv).contains("Position 1,P1");
    }

    @Test
    void exportUsers_includesSkillColumn() {
        User user = TestEntityFactory.user(1L);
        when(userRepository.findAll(Sort.by("id").ascending())).thenReturn(List.of(user));
        when(skillRepository.findAll()).thenReturn(List.of());

        String csv = csvExportService.exportUsers();

        assertThat(csv).contains("ID,Name,Email,Birthday,Role,Team,Position,Skills");
        assertThat(csv).contains(user.getEmail());
    }

    @Test
    void exportActivityLogs_includesSystemUserLabel() {
        ActivityLog log = ActivityLog.builder()
                .id(1L)
                .action("LOGIN")
                .description("Test")
                .createdAt(LocalDateTime.of(2024, 1, 1, 10, 0))
                .build();
        when(activityLogRepository.findAll(Sort.by("createdAt").descending())).thenReturn(List.of(log));

        String csv = csvExportService.exportActivityLogs();

        assertThat(csv).contains("Hệ thống");
        assertThat(csv).contains("LOGIN");
    }

    @Test
    void exportSkills_includesUserName() {
        User user = TestEntityFactory.user(1L);
        Skill skill = TestEntityFactory.skill(1L, user);
        when(skillRepository.findAll(Sort.by("id").ascending())).thenReturn(List.of(skill));

        String csv = csvExportService.exportSkills();

        assertThat(csv).contains("Java");
        assertThat(csv).contains(user.getName());
    }

    @Test
    void exportTeams_includesLeaderAndMemberCount() {
        User leader = TestEntityFactory.user(1L);
        Team team = TestEntityFactory.team(2L);
        team.setLeader(leader);
        when(teamRepository.findAll(Sort.by("id").ascending())).thenReturn(List.of(team));
        when(userRepository.countByTeamId(2L)).thenReturn(5L);

        String csv = csvExportService.exportTeams();

        assertThat(csv).contains("Team 2");
        assertThat(csv).contains(leader.getName());
        assertThat(csv).contains("5");
    }

    @Test
    void exportProjects_includesMembers() {
        Team team = TestEntityFactory.team(1L);
        User member = TestEntityFactory.user(2L);
        Project project = TestEntityFactory.project(3L, team);
        project.setStartDate(LocalDate.of(2024, 1, 1));
        project.setEndDate(LocalDate.of(2024, 12, 31));
        ProjectMember pm = TestEntityFactory.projectMember(project, member);
        when(projectRepository.findAll(Sort.by("id").ascending())).thenReturn(List.of(project));
        when(projectMemberRepository.findAll()).thenReturn(List.of(pm));

        String csv = csvExportService.exportProjects();

        assertThat(csv).contains("Project 3");
        assertThat(csv).contains(member.getName());
    }

    @Test
    void exportUsers_withSkillLabel_formatsLevelAndYears() {
        User user = TestEntityFactory.user(1L);
        Skill skill = Skill.builder().name("Spring").level("Mid").usedYearNumber(3).user(user).build();
        when(userRepository.findAll(Sort.by("id").ascending())).thenReturn(List.of(user));
        when(skillRepository.findAll()).thenReturn(List.of(skill));

        String csv = csvExportService.exportUsers();

        assertThat(csv).contains("Spring (Mid, 3y)");
    }
}
