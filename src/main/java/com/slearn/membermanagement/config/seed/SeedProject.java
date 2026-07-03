package com.slearn.membermanagement.config.seed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SeedProject(
        String key,
        String name,
        String abbreviation,
        String startDate,
        String endDate,
        String teamKey,
        String leaderKey,
        List<String> memberKeys
) {
}
