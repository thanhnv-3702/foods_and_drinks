package com.slearn.membermanagement.config;

import com.slearn.membermanagement.config.seed.SeedDataLoader;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class DataInitializer {

    @Bean
    public CommandLineRunner seedData(SeedDataLoader seedDataLoader) {
        return args -> seedDataLoader.loadIfNeeded();
    }
}
