package com.slearn.membermanagement.controller;

import com.slearn.membermanagement.security.CustomUserDetails;
import com.slearn.membermanagement.service.ProfileService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        Long userId = principal.getUser().getId();
        model.addAttribute("profile", profileService.getProfile(userId));
        model.addAttribute("activeMenu", "profile");
        return "client/profile";
    }
}
