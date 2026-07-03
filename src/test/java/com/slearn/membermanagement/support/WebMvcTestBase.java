package com.slearn.membermanagement.support;

import com.slearn.membermanagement.service.MessageService;
import com.slearn.membermanagement.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

/**
 * Shared mocks required by {@code @ControllerAdvice} beans (e.g. ClientGlobalAttributes)
 * when using {@code @WebMvcTest} on develop (T18 notifications).
 */
public abstract class WebMvcTestBase {

    @MockBean
    protected NotificationService notificationService;

    @MockBean
    protected MessageService messages;

    @BeforeEach
    void stubMessages() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");

        Answer<String> answer = invocation -> {
            String code = invocation.getArgument(0);
            Object[] args = new Object[invocation.getArguments().length - 1];
            System.arraycopy(invocation.getArguments(), 1, args, 0, args.length);
            return messageSource.getMessage(code, args.length == 0 ? null : args, Locale.ENGLISH);
        };

        lenient().when(messages.get(anyString())).thenAnswer(answer);
        lenient().when(messages.get(anyString(), any())).thenAnswer(answer);
    }
}
