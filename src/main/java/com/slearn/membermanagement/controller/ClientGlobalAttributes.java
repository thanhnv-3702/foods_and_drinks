package com.slearn.membermanagement.controller;

import com.slearn.membermanagement.security.CustomUserDetails;
import com.slearn.membermanagement.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ClientGlobalAttributes {

    private final NotificationService notificationService;

    public ClientGlobalAttributes(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @ModelAttribute("unreadNotificationCount")
    public long unreadNotificationCount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return notificationService.countMyUnread();
        }
        return 0L;
    }
}
