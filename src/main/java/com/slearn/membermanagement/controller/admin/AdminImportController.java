package com.slearn.membermanagement.controller.admin;

import com.slearn.membermanagement.dto.ImportResult;
import com.slearn.membermanagement.service.CsvImportService;
import com.slearn.membermanagement.service.MessageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/import")
public class AdminImportController {

    private final CsvImportService csvImportService;
    private final MessageService messages;

    public AdminImportController(CsvImportService csvImportService, MessageService messages) {
        this.csvImportService = csvImportService;
        this.messages = messages;
    }

    @GetMapping
    public String page(Model model) {
        model.addAttribute("pageTitle", messages.get("page.import"));
        model.addAttribute("activeMenu", "import");
        return "admin/import/index";
    }

    @PostMapping("/{entity}")
    public String importEntity(@PathVariable String entity,
                               @RequestParam("file") MultipartFile file,
                               RedirectAttributes ra) {
        if (file == null || file.isEmpty()) {
            ra.addFlashAttribute("errorMessage", messages.get("flash.import.noFile"));
            return "redirect:/admin/import";
        }
        try {
            ImportResult result = switch (entity) {
                case "positions" -> csvImportService.importPositions(file);
                case "skills" -> csvImportService.importSkills(file);
                case "teams" -> csvImportService.importTeams(file);
                case "users" -> csvImportService.importUsers(file);
                case "projects" -> csvImportService.importProjects(file);
                default -> null;
            };
            if (result == null) {
                ra.addFlashAttribute("errorMessage", messages.get("flash.import.invalidEntity", entity));
            } else {
                ra.addFlashAttribute("importResult", result);
                ra.addFlashAttribute("importEntity", entity);
            }
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", messages.get("flash.import.readError", ex.getMessage()));
        }
        return "redirect:/admin/import";
    }
}
