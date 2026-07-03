package com.slearn.membermanagement.controller.admin;

import com.slearn.membermanagement.service.TeamMemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/teams/{teamId}/members")
public class AdminTeamMemberController {

    private final TeamMemberService teamMemberService;

    public AdminTeamMemberController(TeamMemberService teamMemberService) {
        this.teamMemberService = teamMemberService;
    }

    @GetMapping
    public String members(@PathVariable Long teamId, Model model) {
        model.addAttribute("team", teamMemberService.getTeam(teamId));
        model.addAttribute("members", teamMemberService.getMembers(teamId));
        model.addAttribute("candidates", teamMemberService.getCandidates(teamId));
        model.addAttribute("histories", teamMemberService.getHistory(teamId));
        model.addAttribute("pageTitle", "Quản lý thành viên");
        model.addAttribute("activeMenu", "teams");
        return "admin/teams/members";
    }

    @PostMapping("/add")
    public String addMember(@PathVariable Long teamId,
                            @RequestParam(required = false) Long userId,
                            RedirectAttributes ra) {
        if (userId == null) {
            ra.addFlashAttribute("errorMessage", "Vui lòng chọn thành viên.");
            return "redirect:/admin/teams/" + teamId + "/members";
        }
        teamMemberService.addOrMoveMember(teamId, userId);
        ra.addFlashAttribute("successMessage", "Đã thêm/di chuyển thành viên vào team.");
        return "redirect:/admin/teams/" + teamId + "/members";
    }

    @PostMapping("/{userId}/remove")
    public String removeMember(@PathVariable Long teamId,
                               @PathVariable Long userId,
                               RedirectAttributes ra) {
        teamMemberService.removeMember(teamId, userId);
        ra.addFlashAttribute("successMessage", "Đã gỡ thành viên khỏi team.");
        return "redirect:/admin/teams/" + teamId + "/members";
    }
}
