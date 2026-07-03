package com.slearn.membermanagement.exception;

import com.slearn.membermanagement.service.MessageService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private final MessageService messages = mock(MessageService.class);
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler(messages);

    @BeforeEach
    void stubMessages() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");

        lenient().when(messages.get(anyString())).thenAnswer(invocation ->
                messageSource.getMessage(invocation.getArgument(0), null, Locale.ENGLISH));
        lenient().when(messages.get(anyString(), any())).thenAnswer(invocation -> {
            String code = invocation.getArgument(0);
            Object[] args = new Object[invocation.getArguments().length - 1];
            System.arraycopy(invocation.getArguments(), 1, args, 0, args.length);
            return messageSource.getMessage(code, args.length == 0 ? null : args, Locale.ENGLISH);
        });
    }

    @Test
    void handleNotFound_returns404ViewWithMessage() {
        Model model = new ConcurrentModel();

        String view = handler.handleNotFound(new EntityNotFoundException("Not found"), model);

        assertThat(view).isEqualTo("error/404");
        assertThat(model.getAttribute("message")).isEqualTo("Not found");
    }

    @Test
    void handleGeneral_returns500ViewWithGenericMessage() {
        Model model = new ConcurrentModel();

        String view = handler.handleGeneral(new RuntimeException("boom"), model);

        assertThat(view).isEqualTo("error/500");
        assertThat(model.getAttribute("message"))
                .isEqualTo("An unexpected error occurred. Please try again later.");
    }
}
