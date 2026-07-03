package com.slearn.membermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionForm {

    private Long id;

    @NotBlank(message = "{validation.position.name.required}")
    @Size(max = 150, message = "{validation.position.name.max}")
    private String name;

    @Size(max = 50, message = "{validation.position.abbr.max}")
    private String abbreviation;
}
