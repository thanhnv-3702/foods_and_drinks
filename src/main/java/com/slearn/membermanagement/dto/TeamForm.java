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
public class TeamForm {

    private Long id;

    @NotBlank(message = "{validation.team.name.required}")
    @Size(max = 150, message = "{validation.team.name.max}")
    private String name;

    @Size(max = 500, message = "{validation.description.max}")
    private String description;

    private Long leaderId;
}
