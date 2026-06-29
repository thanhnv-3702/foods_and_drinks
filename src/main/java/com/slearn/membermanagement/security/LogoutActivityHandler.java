package com.slearn.membermanagement.security;

import com.slearn.membermanagement.service.ActivityLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

/**
 * Ghi log khi người dùng đăng xuất (chạy trước khi SecurityContext bị xóa).
 */
@Component
public class LogoutActivityHandler implements LogoutHandler {

    private final ActivityLogService activityLogService;

    public LogoutActivityHandler(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails details) {
            activityLogService.record(details.getUser().getId(),
                    "LOGOUT", "Đăng xuất: " + details.getUsername());
        }
    }
}
