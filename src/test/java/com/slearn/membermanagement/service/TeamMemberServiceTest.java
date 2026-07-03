package com.slearn.membermanagement.service;

import com.slearn.membermanagement.entity.Team;
import com.slearn.membermanagement.entity.TeamMemberHistory;
import com.slearn.membermanagement.entity.User;
import com.slearn.membermanagement.repository.TeamMemberHistoryRepository;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamMemberServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TeamMemberHistoryRepository historyRepository;

    @Mock
    private ActivityLogService activityLogService;

    @Spy
    private MessageService messages = TestMessageSupport.vietnamese();

    @InjectMocks
    private TeamMemberService teamMemberService;

    @Test
    void addOrMoveMember_assignsTeamAndCreatesHistory() {
        Team team = TestEntityFactory.team(1L);
        User user = TestEntityFactory.user(2L);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(historyRepository.findFirstByUserIdAndLeftAtIsNull(2L)).thenReturn(Optional.empty());

        teamMemberService.addOrMoveMember(1L, 2L);

        verify(userRepository).save(user);
        verify(historyRepository).save(any(TeamMemberHistory.class));
        verify(activityLogService).record(eq("ADD_MEMBER"), contains(user.getName()));
    }

    @Test
    void addOrMoveMember_sameTeam_skips() {
        Team team = TestEntityFactory.team(1L);
        User user = TestEntityFactory.user(2L);
        user.setTeam(team);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        teamMemberService.addOrMoveMember(1L, 2L);

        verify(userRepository, never()).save(any());
        verify(historyRepository, never()).save(any());
    }

    @Test
    void removeMember_clearsTeamAndClosesHistory() {
        Team team = TestEntityFactory.team(1L);
        User user = TestEntityFactory.user(2L);
        user.setTeam(team);
        TeamMemberHistory open = TestEntityFactory.openHistory(team, user);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(historyRepository.findFirstByUserIdAndLeftAtIsNull(2L)).thenReturn(Optional.of(open));

        teamMemberService.removeMember(1L, 2L);

        verify(userRepository).save(user);
        verify(historyRepository).save(open);
        verify(activityLogService).record(eq("REMOVE_MEMBER"), contains(user.getName()));
    }

    @Test
    void addOrMoveMember_userNotFound_throws() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(TestEntityFactory.team(1L)));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teamMemberService.addOrMoveMember(1L, 99L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
