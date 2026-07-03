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
import com.slearn.membermanagement.support.TestEntityFactory;
import com.slearn.membermanagement.support.TestMessageSupport;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActivityLogService activityLogService;

    @Mock
    private NotificationService notificationService;

    @Spy
    private MessageService messages = TestMessageSupport.vietnamese();

    @InjectMocks
    private ProjectService projectService;

    @Test
    void create_savesProjectAndSyncsMembers() {
        Team team = TestEntityFactory.team(1L);
        User member = TestEntityFactory.user(2L);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(userRepository.findById(2L)).thenReturn(Optional.of(member));
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> {
            Project p = inv.getArgument(0);
            p.setId(10L);
            return p;
        });
        when(projectMemberRepository.findByProjectId(10L)).thenReturn(List.of());

        var form = ProjectForm.builder()
                .name("Portal")
                .abbreviation("POR")
                .teamId(1L)
                .memberIds(List.of(2L))
                .build();

        Project created = projectService.create(form);

        assertThat(created.getName()).isEqualTo("Portal");
        verify(projectMemberRepository).saveAll(anyList());
        verify(activityLogService).record(eq("CREATE_PROJECT"), contains("Portal"));
        verify(notificationService).notifyProjectCrud(any(Project.class), eq(NotificationAction.CREATE));
    }

    @Test
    void update_syncsMembersAndNotifies() {
        Team team = TestEntityFactory.team(1L);
        User member = TestEntityFactory.user(2L);
        Project project = TestEntityFactory.project(5L, team);
        when(projectRepository.findById(5L)).thenReturn(Optional.of(project));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(userRepository.findById(2L)).thenReturn(Optional.of(member));
        when(projectMemberRepository.findByProjectId(5L)).thenReturn(List.of());

        var form = ProjectForm.builder()
                .name("Updated")
                .teamId(1L)
                .memberIds(List.of(2L))
                .build();

        Project updated = projectService.update(5L, form);

        assertThat(updated.getName()).isEqualTo("Updated");
        verify(notificationService).notifyProjectCrud(project, NotificationAction.UPDATE);
        verify(activityLogService).record(eq("UPDATE_PROJECT"), contains("Updated"));
    }

    @Test
    void getFormById_mapsMembers() {
        Team team = TestEntityFactory.team(1L);
        Project project = TestEntityFactory.project(3L, team);
        User member = TestEntityFactory.user(2L);
        when(projectRepository.findById(3L)).thenReturn(Optional.of(project));
        when(projectMemberRepository.findByProjectId(3L))
                .thenReturn(List.of(TestEntityFactory.projectMember(project, member)));

        ProjectForm form = projectService.getFormById(3L);

        assertThat(form.getId()).isEqualTo(3L);
        assertThat(form.getMemberIds()).containsExactly(2L);
    }

    @Test
    void findAll_withoutTeamId_returnsAll() {
        when(projectRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        projectService.findAll(null, Pageable.unpaged());

        verify(projectRepository).findAll(Pageable.unpaged());
    }

    @Test
    void findAllTeamsAndUsers_delegateToRepositories() {
        when(teamRepository.findAll()).thenReturn(List.of(TestEntityFactory.team(1L)));
        when(userRepository.findAll()).thenReturn(List.of(TestEntityFactory.user(1L)));

        assertThat(projectService.findAllTeams()).hasSize(1);
        assertThat(projectService.findAllUsers()).hasSize(1);
    }

    @Test
    void syncMembers_removesOldAndAddsNew() {
        Team team = TestEntityFactory.team(1L);
        User oldMember = TestEntityFactory.user(2L);
        User newMember = TestEntityFactory.user(3L);
        Project project = TestEntityFactory.project(10L, team);
        ProjectMember existing = TestEntityFactory.projectMember(project, oldMember);
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(userRepository.findById(3L)).thenReturn(Optional.of(newMember));
        when(projectMemberRepository.findByProjectId(10L)).thenReturn(List.of(existing));

        var form = ProjectForm.builder()
                .name("Portal")
                .teamId(1L)
                .memberIds(List.of(3L))
                .build();

        projectService.update(10L, form);

        verify(projectMemberRepository).delete(existing);
        verify(projectMemberRepository).saveAll(anyList());
    }

    @Test
    void create_teamNotFound_throws() {
        when(teamRepository.findById(99L)).thenReturn(Optional.empty());

        var form = ProjectForm.builder().name("X").teamId(99L).build();

        assertThatThrownBy(() -> projectService.create(form))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void create_memberNotFound_throws() {
        Team team = TestEntityFactory.team(1L);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> {
            Project p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });
        when(projectMemberRepository.findByProjectId(1L)).thenReturn(List.of());

        var form = ProjectForm.builder().name("X").teamId(1L).memberIds(List.of(99L)).build();

        assertThatThrownBy(() -> projectService.create(form))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_notifiesBeforeRemoval() {
        Project project = TestEntityFactory.project(5L, TestEntityFactory.team(1L));
        when(projectRepository.findById(5L)).thenReturn(Optional.of(project));

        projectService.delete(5L);

        verify(notificationService).notifyProjectCrud(project, NotificationAction.DELETE);
        verify(projectMemberRepository).deleteByProjectId(5L);
        verify(projectRepository).delete(project);
    }

    @Test
    void getById_notFound_throws() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getById(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findAll_withTeamId_filtersByTeam() {
        when(projectRepository.findByTeamId(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        projectService.findAll(1L, Pageable.unpaged());

        verify(projectRepository).findByTeamId(1L, Pageable.unpaged());
    }

    @Test
    void memberCountByProject_mapsCounts() {
        when(projectMemberRepository.countGroupedByProject())
                .thenReturn(List.<Object[]>of(new Object[]{10L, 4L}));

        assertThat(projectService.memberCountByProject()).containsEntry(10L, 4L);
    }
}
