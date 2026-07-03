package com.slearn.membermanagement.exception;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_returns404ViewWithMessage() {
        Model model = new ConcurrentModel();

        String view = handler.handleNotFound(new EntityNotFoundException("Không tìm thấy"), model);

        assertThat(view).isEqualTo("error/404");
        assertThat(model.getAttribute("message")).isEqualTo("Không tìm thấy");
    }

    @Test
    void handleGeneral_returns500ViewWithGenericMessage() {
        Model model = new ConcurrentModel();

        String view = handler.handleGeneral(new RuntimeException("boom"), model);

        assertThat(view).isEqualTo("error/500");
        assertThat(model.getAttribute("message"))
                .isEqualTo("Đã xảy ra lỗi trên hệ thống. Vui lòng thử lại sau.");
    }
}
