package com.slearn.membermanagement.security;

import com.slearn.membermanagement.service.ActivityLogService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * Ghi log khi người dùng đăng nhập thành công (form login).
 */
@Component
public class LoginActivityListener {

    private final ActivityLogService activityLogService;

    public LoginActivityListener(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @EventListener
    public void onLoginSuccess(InteractiveAuthenticationSuccessEvent event) {
        if (event.getAuthentication() != null
                && event.getAuthentication().getPrincipal() instanceof CustomUserDetails details) {
            activityLogService.record(details.getUser().getId(),
                    "LOGIN", "Đăng nhập: " + details.getUsername());
        }
    }
}
