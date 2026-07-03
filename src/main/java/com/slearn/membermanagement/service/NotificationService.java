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
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class NotificationService {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final NotificationRepository notificationRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final MessageService messages;

    public NotificationService(NotificationRepository notificationRepository,
                               ProjectMemberRepository projectMemberRepository,
                               UserRepository userRepository,
                               MessageService messages) {
        this.notificationRepository = notificationRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
        this.messages = messages;
    }

    @Transactional(readOnly = true)
    public Page<Notification> findMyNotifications(Boolean read, Pageable pageable) {
        Long userId = currentUserId();
        if (read == null) {
            return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId, pageable);
        }
        return notificationRepository.findByRecipientIdAndReadOrderByCreatedAtDesc(userId, read, pageable);
    }

    @Transactional(readOnly = true)
    public Notification getMyNotification(Long id) {
        Long userId = currentUserId();
        return notificationRepository.findByIdAndRecipientId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException(messages.get("error.notification.notFound", id)));
    }

    @Transactional
    public void markAsRead(Long id) {
        Notification notification = getMyNotification(id);
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void deleteMyNotification(Long id) {
        Long notificationId = Objects.requireNonNull(id, "Notification id is required");
        getMyNotification(notificationId);
        notificationRepository.deleteById(notificationId);
    }

    @Transactional
    public Notification openMyNotification(Long id) {
        Notification notification = getMyNotification(id);
        if (!notification.isRead()) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
        return notification;
    }

    @Transactional(readOnly = true)
    public long countMyUnread() {
        return notificationRepository.countUnreadByRecipientId(currentUserId());
    }

    @Transactional
    public void notifyTeamCrud(Team team, NotificationAction action) {
        Set<User> recipients = new LinkedHashSet<>();
        List<User> teamMembers = userRepository.findByTeamIdOrderByNameAsc(team.getId());
        recipients.addAll(teamMembers);
        if (team.getLeader() != null) {
            recipients.add(team.getLeader());
        }
        if (recipients.isEmpty()) {
            return;
        }

        String title = buildTitle(NotificationType.TEAM, action, team.getName());
        String content = messages.get("notification.content.team",
                team.getName(),
                managerName(team.getLeader()),
                actorName(),
                TIME_FORMAT.format(LocalDateTime.now()));

        saveNotifications(recipients, NotificationType.TEAM, action, title, content);
    }

    @Transactional
    public void notifyProjectCrud(Project project, NotificationAction action) {
        Set<User> recipients = new LinkedHashSet<>();
        List<ProjectMember> memberships = projectMemberRepository.findByProjectId(project.getId());
        for (ProjectMember membership : memberships) {
            recipients.add(membership.getUser());
        }
        if (project.getLeader() != null) {
            recipients.add(project.getLeader());
        }
        if (recipients.isEmpty()) {
            return;
        }

        String title = buildTitle(NotificationType.PROJECT, action, project.getName());
        String content = messages.get("notification.content.project",
                project.getName(),
                managerName(project.getLeader()),
                actorName(),
                TIME_FORMAT.format(LocalDateTime.now()));

        saveNotifications(recipients, NotificationType.PROJECT, action, title, content);
    }

    private void saveNotifications(Set<User> recipients, NotificationType type, NotificationAction action,
                                   String title, String content) {
        for (User recipient : recipients) {
            Notification notification = Objects.requireNonNull(Notification.builder()
                    .recipient(recipient)
                    .title(title)
                    .content(content)
                    .type(type)
                    .action(action)
                    .read(false)
                    .build());
            notificationRepository.save(notification);
        }
    }

    private String buildTitle(NotificationType type, NotificationAction action, String targetName) {
        return switch (action) {
            case CREATE -> messages.get("notification.title.created", type.name(), targetName);
            case UPDATE -> messages.get("notification.title.updated", type.name(), targetName);
            case DELETE -> messages.get("notification.title.deleted", type.name(), targetName);
        };
    }

    private String managerName(User manager) {
        return manager != null ? manager.getName() : messages.get("notification.leader.none");
    }

    private String actorName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails details) {
            return details.getDisplayName();
        }
        return messages.get("notification.actor.system");
    }

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails details) {
            return details.getUser().getId();
        }
        throw new IllegalStateException(messages.get("error.auth.noCurrentUser"));
    }
}
