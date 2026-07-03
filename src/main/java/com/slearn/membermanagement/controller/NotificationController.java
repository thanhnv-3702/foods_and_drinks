package com.slearn.membermanagement.controller;

import com.slearn.membermanagement.service.MessageService;
import com.slearn.membermanagement.service.NotificationService;
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
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final MessageService messages;

    public NotificationController(NotificationService notificationService, MessageService messages) {
        this.notificationService = notificationService;
        this.messages = messages;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) Boolean read,
                       Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<?> notifications = notificationService.findMyNotifications(read, pageable);

        model.addAttribute("notifications", notifications);
        model.addAttribute("selectedRead", read);
        model.addAttribute("unreadCount", notificationService.countMyUnread());
        model.addAttribute("pageTitle", messages.get("page.notifications"));
        model.addAttribute("activeMenu", "notifications");
        return "client/notifications/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        var notification = notificationService.openMyNotification(id);
        model.addAttribute("notification", notification);
        model.addAttribute("pageTitle", messages.get("page.notification.detail"));
        model.addAttribute("activeMenu", "notifications");
        return "client/notifications/detail";
    }

    @PostMapping("/{id}/read")
    public String markAsRead(@PathVariable Long id, RedirectAttributes ra) {
        notificationService.markAsRead(id);
        ra.addFlashAttribute("successMessage", messages.get("flash.notification.read"));
        return "redirect:/notifications/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        notificationService.deleteMyNotification(id);
        ra.addFlashAttribute("successMessage", messages.get("flash.notification.deleted"));
        return "redirect:/notifications";
    }
}
