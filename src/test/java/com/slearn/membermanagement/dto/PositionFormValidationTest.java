package com.slearn.membermanagement.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PositionFormValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void blankName_isInvalid() {
        var form = PositionForm.builder().name("").abbreviation("DEV").build();

        var violations = validator.validate(form);

        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).isEqualTo("{validation.position.name.required}");
    }

    @Test
    void validForm_passes() {
        var form = PositionForm.builder().name("Developer").abbreviation("DEV").build();

        assertThat(validator.validate(form)).isEmpty();
    }
}
