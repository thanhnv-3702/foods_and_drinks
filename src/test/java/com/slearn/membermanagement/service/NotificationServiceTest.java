package com.slearn.membermanagement.service;

import com.slearn.membermanagement.entity.Notification;
import com.slearn.membermanagement.entity.NotificationAction;
import com.slearn.membermanagement.entity.NotificationType;
import com.slearn.membermanagement.entity.Team;
import com.slearn.membermanagement.entity.User;
import com.slearn.membermanagement.repository.NotificationRepository;
import com.slearn.membermanagement.repository.ProjectMemberRepository;
import com.slearn.membermanagement.repository.UserRepository;
import com.slearn.membermanagement.security.CustomUserDetails;
import com.slearn.membermanagement.support.TestEntityFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void countMyUnread_usesCurrentUser() {
        var user = TestEntityFactory.user(1L);
        setAuth(user);
        when(notificationRepository.countUnreadByRecipientId(1L)).thenReturn(3L);

        assertThat(notificationService.countMyUnread()).isEqualTo(3L);
    }

    @Test
    void markAsRead_setsReadFlag() {
        var user = TestEntityFactory.user(1L);
        setAuth(user);
        Notification n = Notification.builder().id(5L).recipient(user).read(false).build();
        when(notificationRepository.findByIdAndRecipientId(5L, 1L)).thenReturn(Optional.of(n));

        notificationService.markAsRead(5L);

        assertThat(n.isRead()).isTrue();
        verify(notificationRepository).save(n);
    }

    @Test
    void getMyNotification_notFound_throws() {
        var user = TestEntityFactory.user(1L);
        setAuth(user);
        when(notificationRepository.findByIdAndRecipientId(9L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.getMyNotification(9L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findMyNotifications_withReadFilter() {
        var user = TestEntityFactory.user(2L);
        setAuth(user);
        var page = new PageImpl<Notification>(List.of());
        when(notificationRepository.findByRecipientIdAndReadOrderByCreatedAtDesc(
                eq(2L), eq(false), any(Pageable.class))).thenReturn(page);

        assertThat(notificationService.findMyNotifications(false, Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    void notifyTeamCrud_savesForMembers() {
        Team team = TestEntityFactory.team(1L);
        User member = TestEntityFactory.user(2L);
        when(userRepository.findByTeamIdOrderByNameAsc(1L)).thenReturn(List.of(member));

        notificationService.notifyTeamCrud(team, NotificationAction.CREATE);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertThat(captor.getValue().getType()).isEqualTo(NotificationType.TEAM);
        assertThat(captor.getValue().getRecipient()).isEqualTo(member);
    }

    private void setAuth(User user) {
        var auth = new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(user), null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
