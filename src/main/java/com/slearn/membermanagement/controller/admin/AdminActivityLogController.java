package com.slearn.membermanagement.controller.admin;

import com.slearn.membermanagement.entity.ActivityLog;
import com.slearn.membermanagement.service.ActivityLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/activity-logs")
public class AdminActivityLogController {

    private final ActivityLogService activityLogService;

    public AdminActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "15") int size,
                       Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ActivityLog> logs = activityLogService.findAll(pageable);
        model.addAttribute("logs", logs);
        model.addAttribute("pageTitle", "Activity Logs");
        model.addAttribute("activeMenu", "activity-logs");
        return "admin/activity-logs/list";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        activityLogService.delete(id);
        ra.addFlashAttribute("successMessage", "Đã xóa log.");
        return "redirect:/admin/activity-logs";
    }

    @PostMapping("/clear")
    public String clear(RedirectAttributes ra) {
        activityLogService.deleteAll();
        ra.addFlashAttribute("successMessage", "Đã xóa toàn bộ log.");
        return "redirect:/admin/activity-logs";
    }
}
