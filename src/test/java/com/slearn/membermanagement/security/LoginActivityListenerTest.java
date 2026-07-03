package com.slearn.membermanagement.security;

import com.slearn.membermanagement.service.ActivityLogService;
import com.slearn.membermanagement.support.TestEntityFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoginActivityListenerTest {

    @Mock
    private ActivityLogService activityLogService;

    @InjectMocks
    private LoginActivityListener loginActivityListener;

    @Test
    void onLoginSuccess_recordsActivity() {
        var user = TestEntityFactory.user(1L);
        var details = new CustomUserDetails(user);
        var auth = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
        var event = new InteractiveAuthenticationSuccessEvent(auth, getClass());

        loginActivityListener.onLoginSuccess(event);

        verify(activityLogService).record(eq(1L), eq("LOGIN"), contains(user.getEmail()));
    }
}
