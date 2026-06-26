package com.slearn.membermanagement.controller.admin;

import com.slearn.membermanagement.dto.PositionForm;
import com.slearn.membermanagement.service.PositionService;
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
@RequestMapping("/admin/positions")
public class AdminPositionController {

    private final PositionService positionService;

    public AdminPositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<?> positions = positionService.findAll(pageable);
        model.addAttribute("positions", positions);
        model.addAttribute("pageTitle", "Positions");
        model.addAttribute("activeMenu", "positions");
        return "admin/positions/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("positionForm", new PositionForm());
        model.addAttribute("pageTitle", "Tạo Position");
        model.addAttribute("activeMenu", "positions");
        return "admin/positions/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("positionForm") PositionForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Tạo Position");
            model.addAttribute("activeMenu", "positions");
            return "admin/positions/form";
        }
        positionService.create(form);
        ra.addFlashAttribute("successMessage", "Đã tạo vị trí thành công.");
        return "redirect:/admin/positions";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("positionForm", positionService.getFormById(id));
        model.addAttribute("pageTitle", "Sửa Position");
        model.addAttribute("activeMenu", "positions");
        return "admin/positions/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("positionForm") PositionForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Sửa Position");
            model.addAttribute("activeMenu", "positions");
            return "admin/positions/form";
        }
        positionService.update(id, form);
        ra.addFlashAttribute("successMessage", "Đã cập nhật vị trí thành công.");
        return "redirect:/admin/positions";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        positionService.delete(id);
        ra.addFlashAttribute("successMessage", "Đã xóa vị trí.");
        return "redirect:/admin/positions";
    }
}
