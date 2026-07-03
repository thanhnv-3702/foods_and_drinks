package com.slearn.membermanagement.security;

import com.slearn.membermanagement.entity.User;
import com.slearn.membermanagement.repository.UserRepository;
import com.slearn.membermanagement.service.MessageService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final MessageService messages;

    public CustomUserDetailsService(UserRepository userRepository, MessageService messages) {
        this.userRepository = userRepository;
        this.messages = messages;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        messages.get("error.user.emailNotFound", email)));
        return new CustomUserDetails(user);
    }
}
