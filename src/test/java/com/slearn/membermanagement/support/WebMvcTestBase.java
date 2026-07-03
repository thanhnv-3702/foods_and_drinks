package com.slearn.membermanagement.support;

import com.slearn.membermanagement.service.NotificationService;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * Shared mocks required by {@code @ControllerAdvice} beans (e.g. ClientGlobalAttributes)
 * when using {@code @WebMvcTest} on develop (T18 notifications).
 */
public abstract class WebMvcTestBase {

    @MockBean
    protected NotificationService notificationService;
}
