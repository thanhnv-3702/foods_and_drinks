package com.slearn.membermanagement.service;

import com.slearn.membermanagement.entity.ActivityLog;
import com.slearn.membermanagement.entity.Position;
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
}
