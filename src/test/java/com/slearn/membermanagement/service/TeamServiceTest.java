package com.slearn.membermanagement.service;

import com.slearn.membermanagement.dto.TeamForm;
import com.slearn.membermanagement.entity.Team;
import com.slearn.membermanagement.repository.TeamRepository;
import com.slearn.membermanagement.repository.UserRepository;
import com.slearn.membermanagement.support.TestEntityFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActivityLogService activityLogService;

    @InjectMocks
    private TeamService teamService;

    @Test
    void create_savesTeamWithoutLeader() {
        var form = TeamForm.builder().name("Alpha").description("Team A").build();
        when(teamRepository.save(any(Team.class))).thenAnswer(inv -> {
            Team t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });

        Team created = teamService.create(form);

        assertThat(created.getName()).isEqualTo("Alpha");
        assertThat(created.getLeader()).isNull();
        verify(activityLogService).record(eq("CREATE_TEAM"), contains("Alpha"));
    }

    @Test
    void delete_withMembers_returnsErrorMessage() {
        Team team = TestEntityFactory.team(1L);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(userRepository.countByTeamId(1L)).thenReturn(3L);

        String error = teamService.delete(1L);

        assertThat(error).contains("3 thành viên");
        verify(teamRepository, never()).delete(any());
    }

    @Test
    void delete_withoutMembers_succeeds() {
        Team team = TestEntityFactory.team(2L);
        when(teamRepository.findById(2L)).thenReturn(Optional.of(team));
        when(userRepository.countByTeamId(2L)).thenReturn(0L);

        String error = teamService.delete(2L);

        assertThat(error).isNull();
        verify(teamRepository).delete(team);
        verify(activityLogService).record(eq("DELETE_TEAM"), contains("2"));
    }

    @Test
    void update_leaderNotFound_throws() {
        Team team = TestEntityFactory.team(1L);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        var form = TeamForm.builder().name("Beta").leaderId(99L).build();

        assertThatThrownBy(() -> teamService.update(1L, form))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
