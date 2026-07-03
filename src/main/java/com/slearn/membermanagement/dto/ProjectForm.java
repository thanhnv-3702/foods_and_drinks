package com.slearn.membermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectForm {

    private Long id;

    @NotBlank(message = "{validation.project.name.required}")
    @Size(max = 150, message = "{validation.project.name.max}")
    private String name;

    @Size(max = 50, message = "{validation.abbreviation.max}")
    private String abbreviation;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @NotNull(message = "{validation.team.required}")
    private Long teamId;

    private Long leaderId;

    @Builder.Default
    private List<Long> memberIds = new ArrayList<>();
}
