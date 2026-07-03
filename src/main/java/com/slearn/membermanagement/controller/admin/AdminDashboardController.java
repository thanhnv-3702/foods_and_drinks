package com.slearn.membermanagement.controller.admin;

import com.slearn.membermanagement.service.MessageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final MessageService messages;

    public AdminDashboardController(MessageService messages) {
        this.messages = messages;
    }

    @GetMapping({"", "/"})
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", messages.get("page.dashboard"));
        model.addAttribute("activeMenu", "dashboard");
        return "admin/dashboard";
    }
}
