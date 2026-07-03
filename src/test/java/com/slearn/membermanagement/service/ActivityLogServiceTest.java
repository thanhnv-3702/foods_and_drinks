package com.slearn.membermanagement.service;

import com.slearn.membermanagement.entity.ActivityLog;
import com.slearn.membermanagement.entity.User;
import com.slearn.membermanagement.repository.ActivityLogRepository;
import com.slearn.membermanagement.repository.UserRepository;
import com.slearn.membermanagement.security.CustomUserDetails;
import com.slearn.membermanagement.support.TestEntityFactory;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityLogServiceTest {

    @Mock
    private ActivityLogRepository activityLogRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ActivityLogService activityLogService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void record_withUserId_persistsLog() {
        User actor = TestEntityFactory.user(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(actor));

        activityLogService.record(1L, "LOGIN", "Đăng nhập");

        ArgumentCaptor<ActivityLog> captor = ArgumentCaptor.forClass(ActivityLog.class);
        verify(activityLogRepository).save(captor.capture());
        ActivityLog saved = captor.getValue();
        assertThat(saved.getAction()).isEqualTo("LOGIN");
        assertThat(saved.getUser()).isEqualTo(actor);
    }

    @Test
    void record_usesSecurityContextUser() {
        User actor = TestEntityFactory.user(2L);
        var auth = new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(actor), null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        activityLogService.record("UPDATE_USER", "Cập nhật");

        ArgumentCaptor<ActivityLog> captor = ArgumentCaptor.forClass(ActivityLog.class);
        verify(activityLogRepository).save(captor.capture());
        assertThat(captor.getValue().getAction()).isEqualTo("UPDATE_USER");
    }

    @Test
    void findAll_delegatesToRepository() {
        var page = new PageImpl<>(List.<ActivityLog>of());
        when(activityLogRepository.findAll(any(Pageable.class))).thenReturn(page);

        assertThat(activityLogService.findAll(Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    void delete_delegatesToRepository() {
        activityLogService.delete(5L);
        verify(activityLogRepository).deleteById(5L);
    }
}
