package com.slearn.membermanagement.security;

import com.slearn.membermanagement.entity.Role;
import com.slearn.membermanagement.support.TestEntityFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomUserDetailsTest {

    @Test
    void usesEmailAsUsername() {
        var user = TestEntityFactory.user(1L);
        var details = new CustomUserDetails(user);

        assertThat(details.getUsername()).isEqualTo(user.getEmail());
        assertThat(details.getPassword()).isEqualTo(user.getPassword());
        assertThat(details.getDisplayName()).isEqualTo(user.getName());
    }

    @Test
    void authoritiesIncludeRolePrefix() {
        var user = TestEntityFactory.user(1L);
        user.setRole(Role.ADMIN);
        var details = new CustomUserDetails(user);

        assertThat(details.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void accountFlagsEnabled() {
        var details = new CustomUserDetails(TestEntityFactory.user(1L));

        assertThat(details.isAccountNonExpired()).isTrue();
        assertThat(details.isAccountNonLocked()).isTrue();
        assertThat(details.isCredentialsNonExpired()).isTrue();
        assertThat(details.isEnabled()).isTrue();
    }
}
