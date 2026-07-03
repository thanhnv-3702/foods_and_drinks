package com.slearn.membermanagement.controller;

import com.slearn.membermanagement.dto.ProfileView;
import com.slearn.membermanagement.security.CustomUserDetails;
import com.slearn.membermanagement.service.ProfileService;
import com.slearn.membermanagement.support.TestEntityFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private ProfileController profileController;

    @Test
    void profile_returnsView() {
        var user = TestEntityFactory.user(1L);
        var principal = new CustomUserDetails(user);
        var profile = ProfileView.builder().id(1L).name("Test").email(user.getEmail()).build();
        when(profileService.getProfile(1L)).thenReturn(profile);
        var model = new ExtendedModelMap();

        String view = profileController.profile(principal, model);

        assertThat(view).isEqualTo("client/profile");
        assertThat(model.getAttribute("activeMenu")).isEqualTo("profile");
        assertThat(model.getAttribute("profile")).isEqualTo(profile);
    }
}
