package com.slearn.membermanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillForm {

    private Long id;

    @NotBlank(message = "{validation.skill.name.required}")
    @Size(max = 150, message = "{validation.skill.name.max}")
    private String name;

    @Size(max = 50, message = "{validation.skill.level.max}")
    private String level;

    @Min(value = 0, message = "{validation.skill.years.min}")
    private Integer usedYearNumber;

    @NotNull(message = "{validation.skill.owner.required}")
    private Long userId;
}
