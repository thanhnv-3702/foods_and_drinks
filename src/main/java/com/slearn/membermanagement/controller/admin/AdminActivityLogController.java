package com.slearn.membermanagement.controller.admin;

import com.slearn.membermanagement.entity.ActivityLog;
import com.slearn.membermanagement.service.ActivityLogService;
import com.slearn.membermanagement.service.MessageService;
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
    private final MessageService messages;

    public AdminActivityLogController(ActivityLogService activityLogService, MessageService messages) {
        this.activityLogService = activityLogService;
        this.messages = messages;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "15") int size,
                       Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ActivityLog> logs = activityLogService.findAll(pageable);
        model.addAttribute("logs", logs);
        model.addAttribute("pageTitle", messages.get("page.activityLogs"));
        model.addAttribute("activeMenu", "activity-logs");
        return "admin/activity-logs/list";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        activityLogService.delete(id);
        ra.addFlashAttribute("successMessage", messages.get("flash.activity.deleted"));
        return "redirect:/admin/activity-logs";
    }

    @PostMapping("/clear")
    public String clear(RedirectAttributes ra) {
        activityLogService.deleteAll();
        ra.addFlashAttribute("successMessage", messages.get("flash.activity.cleared"));
        return "redirect:/admin/activity-logs";
    }
}
