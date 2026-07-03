package com.slearn.membermanagement.controller.admin;

import com.slearn.membermanagement.dto.UserForm;
import com.slearn.membermanagement.entity.Role;
import com.slearn.membermanagement.security.CustomUserDetails;
import com.slearn.membermanagement.service.MessageService;
import com.slearn.membermanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;
    private final MessageService messages;

    public AdminUserController(UserService userService, MessageService messages) {
        this.userService = userService;
        this.messages = messages;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<?> users = userService.findAll(pageable);
        model.addAttribute("users", users);
        model.addAttribute("pageTitle", messages.get("page.users"));
        model.addAttribute("activeMenu", "users");
        return "admin/users/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("userForm", UserForm.builder().role(Role.USER).build());
        populateOptions(model);
        model.addAttribute("pageTitle", messages.get("page.user.create"));
        model.addAttribute("activeMenu", "users");
        return "admin/users/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("userForm") UserForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes ra) {
        validateOnCreate(form, bindingResult);
        if (bindingResult.hasErrors()) {
            populateOptions(model);
            model.addAttribute("pageTitle", messages.get("page.user.create"));
            model.addAttribute("activeMenu", "users");
            return "admin/users/form";
        }
        userService.create(form);
        ra.addFlashAttribute("successMessage", messages.get("flash.user.created"));
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("userForm", userService.getFormById(id));
        populateOptions(model);
        model.addAttribute("pageTitle", messages.get("page.user.edit"));
        model.addAttribute("activeMenu", "users");
        return "admin/users/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("userForm") UserForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes ra) {
        validateOnUpdate(id, form, bindingResult);
        if (bindingResult.hasErrors()) {
            populateOptions(model);
            model.addAttribute("pageTitle", messages.get("page.user.edit"));
            model.addAttribute("activeMenu", "users");
            return "admin/users/form";
        }
        userService.update(id, form);
        ra.addFlashAttribute("successMessage", messages.get("flash.user.updated"));
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal CustomUserDetails principal,
                         RedirectAttributes ra) {
        Long currentUserId = principal != null ? principal.getUser().getId() : null;
        String error = userService.delete(id, currentUserId);
        if (error != null) {
            ra.addFlashAttribute("errorMessage", error);
        } else {
            ra.addFlashAttribute("successMessage", messages.get("flash.user.deleted"));
        }
        return "redirect:/admin/users";
    }

    private void populateOptions(Model model) {
        model.addAttribute("teams", userService.findAllTeams());
        model.addAttribute("positions", userService.findAllPositions());
        model.addAttribute("roles", Role.values());
    }

    private void validateOnCreate(UserForm form, BindingResult bindingResult) {
        if (!StringUtils.hasText(form.getPassword())) {
            bindingResult.rejectValue("password", "validation.password.required");
        } else if (form.getPassword().length() < 6) {
            bindingResult.rejectValue("password", "validation.password.min");
        }
        checkEmailUnique(form, null, bindingResult);
    }

    private void validateOnUpdate(Long id, UserForm form, BindingResult bindingResult) {
        if (StringUtils.hasText(form.getPassword()) && form.getPassword().length() < 6) {
            bindingResult.rejectValue("password", "validation.password.min");
        }
        checkEmailUnique(form, id, bindingResult);
    }

    private void checkEmailUnique(UserForm form, Long excludeId, BindingResult bindingResult) {
        if (StringUtils.hasText(form.getEmail())
                && userService.emailExists(form.getEmail(), excludeId)) {
            bindingResult.rejectValue("email", "validation.email.duplicate");
        }
    }
}
