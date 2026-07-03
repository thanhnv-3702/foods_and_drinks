package com.slearn.membermanagement.config.seed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SeedTeamAssignment(
        String userKey,
        String teamKey,
        String positionName,
        int joinedAtOffsetDays
) {
}
