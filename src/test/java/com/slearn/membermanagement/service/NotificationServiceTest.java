package com.slearn.membermanagement.service;

import com.slearn.membermanagement.entity.Notification;
import com.slearn.membermanagement.entity.NotificationAction;
import com.slearn.membermanagement.entity.NotificationType;
import com.slearn.membermanagement.entity.Project;
import com.slearn.membermanagement.entity.ProjectMember;
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
import static org.mockito.Mockito.never;
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
        User leader = TestEntityFactory.user(3L);
        team.setLeader(leader);
        setAuth(member);
        when(userRepository.findByTeamIdOrderByNameAsc(1L)).thenReturn(List.of(member));

        notificationService.notifyTeamCrud(team, NotificationAction.CREATE);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, org.mockito.Mockito.atLeastOnce()).save(captor.capture());
        assertThat(captor.getAllValues()).anyMatch(n ->
                n.getType() == NotificationType.TEAM && n.getRecipient().equals(member));
    }

    @Test
    void notifyTeamCrud_emptyRecipients_skipsSave() {
        Team team = TestEntityFactory.team(1L);
        when(userRepository.findByTeamIdOrderByNameAsc(1L)).thenReturn(List.of());

        notificationService.notifyTeamCrud(team, NotificationAction.UPDATE);

        verify(notificationRepository, never()).save(any());
    }

    @Test
    void notifyTeamCrud_deleteAction_buildsTitle() {
        Team team = TestEntityFactory.team(1L);
        User member = TestEntityFactory.user(2L);
        when(userRepository.findByTeamIdOrderByNameAsc(1L)).thenReturn(List.of(member));

        notificationService.notifyTeamCrud(team, NotificationAction.DELETE);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertThat(captor.getValue().getTitle()).contains("Đã xóa").contains("Team 1");
    }

    @Test
    void notifyProjectCrud_savesForMembersAndLeader() {
        Team team = TestEntityFactory.team(1L);
        Project project = TestEntityFactory.project(1L, team);
        User member = TestEntityFactory.user(2L);
        User leader = TestEntityFactory.user(3L);
        project.setLeader(leader);
        setAuth(leader);
        ProjectMember pm = TestEntityFactory.projectMember(project, member);
        when(projectMemberRepository.findByProjectId(1L)).thenReturn(List.of(pm));

        notificationService.notifyProjectCrud(project, NotificationAction.UPDATE);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, org.mockito.Mockito.atLeastOnce()).save(captor.capture());
        assertThat(captor.getAllValues()).anyMatch(n ->
                n.getType() == NotificationType.PROJECT && n.getAction() == NotificationAction.UPDATE);
    }

    @Test
    void findMyNotifications_withoutReadFilter_usesAllQuery() {
        var user = TestEntityFactory.user(2L);
        setAuth(user);
        var page = new PageImpl<Notification>(List.of());
        when(notificationRepository.findByRecipientIdOrderByCreatedAtDesc(eq(2L), any(Pageable.class)))
                .thenReturn(page);

        assertThat(notificationService.findMyNotifications(null, Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    void openMyNotification_unread_marksRead() {
        var user = TestEntityFactory.user(1L);
        setAuth(user);
        Notification n = Notification.builder().id(5L).recipient(user).read(false).build();
        when(notificationRepository.findByIdAndRecipientId(5L, 1L)).thenReturn(Optional.of(n));

        Notification opened = notificationService.openMyNotification(5L);

        assertThat(opened.isRead()).isTrue();
        verify(notificationRepository).save(n);
    }

    @Test
    void openMyNotification_alreadyRead_skipsSave() {
        var user = TestEntityFactory.user(1L);
        setAuth(user);
        Notification n = Notification.builder().id(5L).recipient(user).read(true).build();
        when(notificationRepository.findByIdAndRecipientId(5L, 1L)).thenReturn(Optional.of(n));

        notificationService.openMyNotification(5L);

        verify(notificationRepository, never()).save(any());
    }

    @Test
    void deleteMyNotification_removesRecord() {
        var user = TestEntityFactory.user(1L);
        setAuth(user);
        Notification n = Notification.builder().id(7L).recipient(user).build();
        when(notificationRepository.findByIdAndRecipientId(7L, 1L)).thenReturn(Optional.of(n));

        notificationService.deleteMyNotification(7L);

        verify(notificationRepository).deleteById(7L);
    }

    @Test
    void currentUserId_withoutAuth_throws() {
        assertThatThrownBy(() -> notificationService.countMyUnread())
                .isInstanceOf(IllegalStateException.class);
    }

    private void setAuth(User user) {
        var auth = new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(user), null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
