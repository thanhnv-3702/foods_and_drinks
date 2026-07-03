package com.slearn.membermanagement.config.seed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SeedSkill(String userKey, String name, String level, int usedYearNumber) {
}
