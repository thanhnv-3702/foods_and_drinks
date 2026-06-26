package com.slearn.membermanagement.dto;

import com.slearn.membermanagement.entity.Project;
import com.slearn.membermanagement.entity.Skill;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class ProfileView {

    private Long id;
    private String name;
    private String email;
    private LocalDate birthday;
    private String role;
    private String teamName;
    private String positionName;
    private List<Skill> skills;
    private List<Project> projects;
}
