package com.slearn.membermanagement.security;

import com.slearn.membermanagement.repository.UserRepository;
import com.slearn.membermanagement.support.TestEntityFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_returnsCustomUserDetails() {
        var user = TestEntityFactory.user(1L);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        var details = userDetailsService.loadUserByUsername(user.getEmail());

        assertThat(details).isInstanceOf(CustomUserDetails.class);
        assertThat(details.getUsername()).isEqualTo(user.getEmail());
    }

    @Test
    void loadUserByUsername_notFound_throws() {
        when(userRepository.findByEmail("missing@test.local")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("missing@test.local"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
