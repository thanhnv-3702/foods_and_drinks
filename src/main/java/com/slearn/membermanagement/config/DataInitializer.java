package com.slearn.membermanagement.config;

import com.slearn.membermanagement.entity.Role;
import com.slearn.membermanagement.entity.User;
import com.slearn.membermanagement.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Khởi tạo tài khoản admin mặc định để có thể đăng nhập kiểm thử.
 * Seed dữ liệu đầy đủ sẽ được bổ sung ở T20.
 */
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@slearn.local";
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = User.builder()
                        .name("System Admin")
                        .email(adminEmail)
                        .password(passwordEncoder.encode("Admin@12345"))
                        .role(Role.ADMIN)
                        .build();
                userRepository.save(admin);
            }

            String userEmail = "user@slearn.local";
            if (!userRepository.existsByEmail(userEmail)) {
                User user = User.builder()
                        .name("Demo User")
                        .email(userEmail)
                        .password(passwordEncoder.encode("User@12345"))
                        .role(Role.USER)
                        .build();
                userRepository.save(user);
            }
        };
    }
}
