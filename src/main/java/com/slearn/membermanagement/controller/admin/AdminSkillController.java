package com.slearn.membermanagement.controller.admin;

import com.slearn.membermanagement.dto.SkillForm;
import com.slearn.membermanagement.service.MessageService;
import com.slearn.membermanagement.service.SkillService;
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
@RequestMapping("/admin/skills")
public class AdminSkillController {

    private final SkillService skillService;
    private final MessageService messages;

    public AdminSkillController(SkillService skillService, MessageService messages) {
        this.skillService = skillService;
        this.messages = messages;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<?> skills = skillService.findAll(pageable);
        model.addAttribute("skills", skills);
        model.addAttribute("pageTitle", messages.get("page.skills"));
        model.addAttribute("activeMenu", "skills");
        return "admin/skills/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("skillForm", new SkillForm());
        model.addAttribute("users", skillService.findAllUsers());
        model.addAttribute("pageTitle", messages.get("page.skill.create"));
        model.addAttribute("activeMenu", "skills");
        return "admin/skills/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("skillForm") SkillForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("users", skillService.findAllUsers());
            model.addAttribute("pageTitle", messages.get("page.skill.create"));
            model.addAttribute("activeMenu", "skills");
            return "admin/skills/form";
        }
        skillService.create(form);
        ra.addFlashAttribute("successMessage", messages.get("flash.skill.created"));
        return "redirect:/admin/skills";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("skillForm", skillService.getFormById(id));
        model.addAttribute("users", skillService.findAllUsers());
        model.addAttribute("pageTitle", messages.get("page.skill.edit"));
        model.addAttribute("activeMenu", "skills");
        return "admin/skills/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("skillForm") SkillForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("users", skillService.findAllUsers());
            model.addAttribute("pageTitle", messages.get("page.skill.edit"));
            model.addAttribute("activeMenu", "skills");
            return "admin/skills/form";
        }
        skillService.update(id, form);
        ra.addFlashAttribute("successMessage", messages.get("flash.skill.updated"));
        return "redirect:/admin/skills";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        skillService.delete(id);
        ra.addFlashAttribute("successMessage", messages.get("flash.skill.deleted"));
        return "redirect:/admin/skills";
    }
}
