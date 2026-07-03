package com.slearn.membermanagement.controller.admin;

import com.slearn.membermanagement.dto.TeamForm;
import com.slearn.membermanagement.service.MessageService;
import com.slearn.membermanagement.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/teams")
public class AdminTeamController {

    private final TeamService teamService;
    private final MessageService messages;

    public AdminTeamController(TeamService teamService, MessageService messages) {
        this.teamService = teamService;
        this.messages = messages;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<?> teams = teamService.findAll(pageable);
        model.addAttribute("teams", teams);
        model.addAttribute("pageTitle", messages.get("page.teams"));
        model.addAttribute("activeMenu", "teams");
        return "admin/teams/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("teamForm", new TeamForm());
        model.addAttribute("users", teamService.findAllUsers());
        model.addAttribute("pageTitle", messages.get("page.team.create"));
        model.addAttribute("activeMenu", "teams");
        return "admin/teams/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("teamForm") TeamForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("users", teamService.findAllUsers());
            model.addAttribute("pageTitle", messages.get("page.team.create"));
            model.addAttribute("activeMenu", "teams");
            return "admin/teams/form";
        }
        teamService.create(form);
        ra.addFlashAttribute("successMessage", messages.get("flash.team.created"));
        return "redirect:/admin/teams";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("teamForm", teamService.getFormById(id));
        model.addAttribute("users", teamService.findAllUsers());
        model.addAttribute("pageTitle", messages.get("page.team.edit"));
        model.addAttribute("activeMenu", "teams");
        return "admin/teams/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("teamForm") TeamForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("users", teamService.findAllUsers());
            model.addAttribute("pageTitle", messages.get("page.team.edit"));
            model.addAttribute("activeMenu", "teams");
            return "admin/teams/form";
        }
        teamService.update(id, form);
        ra.addFlashAttribute("successMessage", messages.get("flash.team.updated"));
        return "redirect:/admin/teams";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        String error = teamService.delete(id);
        if (error != null) {
            ra.addFlashAttribute("errorMessage", error);
        } else {
            ra.addFlashAttribute("successMessage", messages.get("flash.team.deleted"));
        }
        return "redirect:/admin/teams";
    }
}
