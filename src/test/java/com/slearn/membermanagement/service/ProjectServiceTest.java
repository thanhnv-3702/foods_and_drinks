package com.slearn.membermanagement.service;

import com.slearn.membermanagement.dto.ProjectForm;
import com.slearn.membermanagement.entity.Project;
import com.slearn.membermanagement.entity.ProjectMember;
import com.slearn.membermanagement.entity.Team;
import com.slearn.membermanagement.entity.User;
import com.slearn.membermanagement.repository.ProjectMemberRepository;
import com.slearn.membermanagement.repository.ProjectRepository;
import com.slearn.membermanagement.repository.TeamRepository;
import com.slearn.membermanagement.repository.UserRepository;
import com.slearn.membermanagement.support.TestEntityFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    }

    @Test
    void delete_removesMembersFirst() {
        Project project = TestEntityFactory.project(5L, TestEntityFactory.team(1L));
        when(projectRepository.findById(5L)).thenReturn(Optional.of(project));

        projectService.delete(5L);

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
    void update_removesMembersNotInDesiredList() {
        Project project = TestEntityFactory.project(1L, TestEntityFactory.team(1L));
        User kept = TestEntityFactory.user(2L);
        User removed = TestEntityFactory.user(3L);
        ProjectMember keptPm = TestEntityFactory.projectMember(project, kept);
        ProjectMember removedPm = TestEntityFactory.projectMember(project, removed);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMemberRepository.findByProjectId(1L)).thenReturn(List.of(keptPm, removedPm));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        var form = ProjectForm.builder()
                .name("Portal")
                .memberIds(List.of(2L))
                .build();

        projectService.update(1L, form);

        verify(projectMemberRepository).delete(removedPm);
    }
}
