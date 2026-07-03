package com.slearn.membermanagement.service;

import com.slearn.membermanagement.entity.Position;
import com.slearn.membermanagement.entity.Project;
import com.slearn.membermanagement.entity.Team;
import com.slearn.membermanagement.entity.User;
import com.slearn.membermanagement.repository.ProjectMemberRepository;
import com.slearn.membermanagement.repository.SkillRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @InjectMocks
    private ProfileService profileService;

    @Test
    void getProfile_mapsUserData() {
        Team team = TestEntityFactory.team(1L);
        Position position = TestEntityFactory.position(2L);
        User user = TestEntityFactory.user(5L);
        user.setTeam(team);
        user.setPosition(position);
        Project project = TestEntityFactory.project(3L, team);
        var pm = TestEntityFactory.projectMember(project, user);
        var skill = TestEntityFactory.skill(1L, user);

        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(projectMemberRepository.findByUserId(5L)).thenReturn(List.of(pm));
        when(skillRepository.findByUserId(5L)).thenReturn(List.of(skill));

        var view = profileService.getProfile(5L);

        assertThat(view.getName()).isEqualTo(user.getName());
        assertThat(view.getTeamName()).isEqualTo(team.getName());
        assertThat(view.getPositionName()).isEqualTo(position.getName());
        assertThat(view.getSkills()).containsExactly(skill);
        assertThat(view.getProjects()).containsExactly(project);
    }

    @Test
    void getProfile_userNotFound_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.getProfile(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
