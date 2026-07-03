package com.slearn.membermanagement.security;

import com.slearn.membermanagement.service.ActivityLogService;
import com.slearn.membermanagement.support.TestEntityFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LogoutActivityHandlerTest {

    @Mock
    private ActivityLogService activityLogService;

    @InjectMocks
    private LogoutActivityHandler logoutActivityHandler;

    @Test
    void logout_recordsActivity() {
        var user = TestEntityFactory.user(3L);
        var details = new CustomUserDetails(user);
        var auth = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());

        logoutActivityHandler.logout(new MockHttpServletRequest(), new MockHttpServletResponse(), auth);

        verify(activityLogService).record(eq(3L), eq("LOGOUT"), contains(user.getEmail()));
    }
}
