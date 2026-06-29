package com.slearn.membermanagement.controller.admin;

import com.slearn.membermanagement.service.CsvExportService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/admin/export")
public class AdminExportController {

    private static final MediaType CSV = MediaType.parseMediaType("text/csv; charset=UTF-8");

    private final CsvExportService csvExportService;

    public AdminExportController(CsvExportService csvExportService) {
        this.csvExportService = csvExportService;
    }

    @GetMapping("/users.csv")
    public ResponseEntity<Resource> exportUsers() {
        return csv("users.csv", csvExportService.exportUsers());
    }

    @GetMapping("/positions.csv")
    public ResponseEntity<Resource> exportPositions() {
        return csv("positions.csv", csvExportService.exportPositions());
    }

    @GetMapping("/skills.csv")
    public ResponseEntity<Resource> exportSkills() {
        return csv("skills.csv", csvExportService.exportSkills());
    }

    @GetMapping("/teams.csv")
    public ResponseEntity<Resource> exportTeams() {
        return csv("teams.csv", csvExportService.exportTeams());
    }

    @GetMapping("/projects.csv")
    public ResponseEntity<Resource> exportProjects() {
        return csv("projects.csv", csvExportService.exportProjects());
    }

    @GetMapping("/activity-logs.csv")
    public ResponseEntity<Resource> exportActivityLogs() {
        return csv("activity-logs.csv", csvExportService.exportActivityLogs());
    }

    private ResponseEntity<Resource> csv(String filename, String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(CSV)
                .contentLength(bytes.length)
                .body(new ByteArrayResource(bytes));
    }
}
