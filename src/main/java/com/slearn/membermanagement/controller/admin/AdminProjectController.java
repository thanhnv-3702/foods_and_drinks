package com.slearn.membermanagement.controller.admin;

import com.slearn.membermanagement.dto.ProjectForm;
import com.slearn.membermanagement.service.ProjectService;
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
@RequestMapping("/admin/projects")
public class AdminProjectController {

    private final ProjectService projectService;

    public AdminProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) Long teamId,
                       Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<?> projects = projectService.findAll(teamId, pageable);
        model.addAttribute("projects", projects);
        model.addAttribute("memberCounts", projectService.memberCountByProject());
        model.addAttribute("teams", projectService.findAllTeams());
        model.addAttribute("selectedTeamId", teamId);
        model.addAttribute("pageTitle", "Projects");
        model.addAttribute("activeMenu", "projects");
        return "admin/projects/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("projectForm", new ProjectForm());
        populateOptions(model);
        model.addAttribute("pageTitle", "Tạo dự án");
        model.addAttribute("activeMenu", "projects");
        return "admin/projects/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("projectForm") ProjectForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes ra) {
        validateDates(form, bindingResult);
        if (bindingResult.hasErrors()) {
            populateOptions(model);
            model.addAttribute("pageTitle", "Tạo dự án");
            model.addAttribute("activeMenu", "projects");
            return "admin/projects/form";
        }
        projectService.create(form);
        ra.addFlashAttribute("successMessage", "Đã tạo dự án thành công.");
        return "redirect:/admin/projects";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("projectForm", projectService.getFormById(id));
        populateOptions(model);
        model.addAttribute("pageTitle", "Sửa dự án");
        model.addAttribute("activeMenu", "projects");
        return "admin/projects/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("projectForm") ProjectForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes ra) {
        validateDates(form, bindingResult);
        if (bindingResult.hasErrors()) {
            populateOptions(model);
            model.addAttribute("pageTitle", "Sửa dự án");
            model.addAttribute("activeMenu", "projects");
            return "admin/projects/form";
        }
        projectService.update(id, form);
        ra.addFlashAttribute("successMessage", "Đã cập nhật dự án thành công.");
        return "redirect:/admin/projects";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        projectService.delete(id);
        ra.addFlashAttribute("successMessage", "Đã xóa dự án và gỡ toàn bộ thành viên.");
        return "redirect:/admin/projects";
    }

    private void populateOptions(Model model) {
        model.addAttribute("teams", projectService.findAllTeams());
        model.addAttribute("users", projectService.findAllUsers());
    }

    private void validateDates(ProjectForm form, BindingResult bindingResult) {
        if (form.getStartDate() != null && form.getEndDate() != null
                && form.getEndDate().isBefore(form.getStartDate())) {
            bindingResult.rejectValue("endDate", "InvalidRange",
                    "Ngày kết thúc phải sau hoặc bằng ngày bắt đầu");
        }
    }
}
