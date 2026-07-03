package com.slearn.membermanagement.config.seed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SeedData(
        String markerEmail,
        String demoPassword,
        List<SeedAccount> accounts,
        List<SeedPosition> positions,
        List<SeedUser> users,
        List<SeedTeam> teams,
        List<SeedTeamAssignment> teamAssignments,
        List<SeedSkill> skills,
        List<SeedProject> projects
) {
}
