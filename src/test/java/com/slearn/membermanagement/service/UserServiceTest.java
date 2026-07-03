package com.slearn.membermanagement.service;

import com.slearn.membermanagement.dto.UserForm;
import com.slearn.membermanagement.entity.Position;
import com.slearn.membermanagement.entity.Role;
import com.slearn.membermanagement.entity.Team;
import com.slearn.membermanagement.entity.User;
import com.slearn.membermanagement.repository.PositionRepository;
import com.slearn.membermanagement.repository.TeamRepository;
import com.slearn.membermanagement.repository.UserRepository;
import com.slearn.membermanagement.support.TestEntityFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ActivityLogService activityLogService;

    @InjectMocks
    private UserService userService;

    @Test
    void create_encodesPasswordAndSaves() {
        when(passwordEncoder.encode("secret")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        var form = UserForm.builder()
                .name("Alice")
                .email("alice@test.local")
                .password("secret")
                .role(Role.USER)
                .build();

        User created = userService.create(form);

        assertThat(created.getPassword()).isEqualTo("encoded");
        verify(activityLogService).record(eq("CREATE_USER"), contains("Alice"));
    }

    @Test
    void update_blankPassword_keepsExisting() {
        User existing = TestEntityFactory.user(1L);
        existing.setPassword("old-hash");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        var form = UserForm.builder()
                .name("Alice")
                .email("alice@test.local")
                .password("")
                .role(Role.USER)
                .build();

        User updated = userService.update(1L, form);

        assertThat(updated.getPassword()).isEqualTo("old-hash");
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void delete_self_returnsError() {
        User user = TestEntityFactory.user(5L);
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        String error = userService.delete(5L, 5L);

        assertThat(error).contains("đang đăng nhập");
        verify(userRepository, never()).delete(any());
    }

    @Test
    void delete_constraintViolation_returnsMessage() {
        User user = TestEntityFactory.user(5L);
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        doThrow(new DataIntegrityViolationException("fk"))
                .when(userRepository).delete(user);

        String error = userService.delete(5L, 1L);

        assertThat(error).contains("tham chiếu");
    }

    @Test
    void getById_notFound_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_success_returnsNull() {
        User user = TestEntityFactory.user(5L);
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        String error = userService.delete(5L, 1L);

        assertThat(error).isNull();
        verify(userRepository).delete(user);
    }

    @Test
    void getFormById_mapsFields() {
        Team team = TestEntityFactory.team(1L);
        Position position = TestEntityFactory.position(2L);
        User user = TestEntityFactory.user(3L);
        user.setTeam(team);
        user.setPosition(position);
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));

        var form = userService.getFormById(3L);

        assertThat(form.getTeamId()).isEqualTo(1L);
        assertThat(form.getPositionId()).isEqualTo(2L);
    }
}
