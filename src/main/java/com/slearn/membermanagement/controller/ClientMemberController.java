package com.slearn.membermanagement.controller;

import com.slearn.membermanagement.entity.Team;
import com.slearn.membermanagement.entity.User;
import com.slearn.membermanagement.service.ClientTeamService;
import com.slearn.membermanagement.service.ProfileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ClientMemberController {

    private final ClientTeamService clientTeamService;
    private final ProfileService profileService;

    public ClientMemberController(ClientTeamService clientTeamService, ProfileService profileService) {
        this.clientTeamService = clientTeamService;
        this.profileService = profileService;
    }

    @GetMapping("/teams/{teamId}/members")
    public String members(@PathVariable Long teamId,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "10") int size,
                          Model model) {
        Team team = clientTeamService.getTeam(teamId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<User> members = clientTeamService.getMembers(teamId, pageable);
        model.addAttribute("team", team);
        model.addAttribute("members", members);
        model.addAttribute("activeMenu", "teams");
        return "client/teams/members";
    }

    @GetMapping("/members/{id}")
    public String memberProfile(@PathVariable Long id, Model model) {
        model.addAttribute("profile", profileService.getProfile(id));
        model.addAttribute("activeMenu", "teams");
        return "client/members/profile";
    }
}
