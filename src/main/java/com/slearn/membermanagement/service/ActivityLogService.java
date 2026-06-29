package com.slearn.membermanagement.service;

import com.slearn.membermanagement.entity.ActivityLog;
import com.slearn.membermanagement.entity.User;
import com.slearn.membermanagement.repository.ActivityLogRepository;
import com.slearn.membermanagement.repository.UserRepository;
import com.slearn.membermanagement.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository, UserRepository userRepository) {
        this.activityLogRepository = activityLogRepository;
        this.userRepository = userRepository;
    }

    /** Ghi log với người thực hiện lấy từ SecurityContext hiện tại. */
    @Transactional
    public void record(String action, String description) {
        record(currentUserId(), action, description);
    }

    /** Ghi log với người thực hiện chỉ định (dùng cho sự kiện login/logout). */
    @Transactional
    public void record(Long userId, String action, String description) {
        User user = (userId != null) ? userRepository.findById(userId).orElse(null) : null;
        ActivityLog log = ActivityLog.builder()
                .action(action)
                .description(description)
                .user(user)
                .build();
        activityLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<ActivityLog> findAll(Pageable pageable) {
        return activityLogRepository.findAll(pageable);
    }

    @Transactional
    public void delete(Long id) {
        activityLogRepository.deleteById(id);
    }

    @Transactional
    public void deleteAll() {
        activityLogRepository.deleteAllInBatch();
    }

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails details) {
            return details.getUser().getId();
        }
        return null;
    }
}
