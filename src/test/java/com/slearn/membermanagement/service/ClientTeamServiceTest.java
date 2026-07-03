package com.slearn.membermanagement.service;

import com.slearn.membermanagement.entity.Project;
import com.slearn.membermanagement.entity.Team;
import com.slearn.membermanagement.entity.User;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientTeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ClientTeamService clientTeamService;

    @Test
    void memberCountByTeam_mapsGroupedCounts() {
        when(userRepository.countGroupedByTeam()).thenReturn(List.of(new Object[]{1L, 5L}, new Object[]{2L, 3L}));

        var counts = clientTeamService.memberCountByTeam();

        assertThat(counts).containsEntry(1L, 5L).containsEntry(2L, 3L);
    }

    @Test
    void getTeam_notFound_throws() {
        when(teamRepository.findWithLeaderById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientTeamService.getTeam(9L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getMembers_paginated_delegatesToRepository() {
        User user = TestEntityFactory.user(1L);
        var page = new PageImpl<>(List.of(user));
        when(userRepository.findByTeamId(eq(1L), any(Pageable.class))).thenReturn(page);

        var result = clientTeamService.getMembers(1L, Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(user);
    }

    @Test
    void getProjects_returnsTeamProjects() {
        Team team = TestEntityFactory.team(1L);
        Project project = TestEntityFactory.project(2L, team);
        when(projectRepository.findByTeamId(1L)).thenReturn(List.of(project));

        assertThat(clientTeamService.getProjects(1L)).containsExactly(project);
    }
}
