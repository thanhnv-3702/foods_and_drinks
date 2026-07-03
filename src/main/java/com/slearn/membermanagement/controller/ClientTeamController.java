package com.slearn.membermanagement.controller;

import com.slearn.membermanagement.entity.Team;
import com.slearn.membermanagement.service.ClientTeamService;
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
public class ClientTeamController {

    private final ClientTeamService clientTeamService;

    public ClientTeamController(ClientTeamService clientTeamService) {
        this.clientTeamService = clientTeamService;
    }

    @GetMapping("/teams")
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "9") int size,
                       Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Team> teams = clientTeamService.findAll(pageable);
        model.addAttribute("teams", teams);
        model.addAttribute("memberCounts", clientTeamService.memberCountByTeam());
        model.addAttribute("activeMenu", "teams");
        return "client/teams/list";
    }

    @GetMapping("/teams/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Team team = clientTeamService.getTeam(id);
        model.addAttribute("team", team);
        model.addAttribute("members", clientTeamService.getMembers(id));
        model.addAttribute("projects", clientTeamService.getProjects(id));
        model.addAttribute("activeMenu", "teams");
        return "client/teams/detail";
    }
}
