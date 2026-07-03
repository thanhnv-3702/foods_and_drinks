package com.slearn.membermanagement.config.seed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SeedPosition(String name, String abbreviation) {
}
