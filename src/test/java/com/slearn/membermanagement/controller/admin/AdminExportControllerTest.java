package com.slearn.membermanagement.controller.admin;

import com.slearn.membermanagement.support.WebMvcTestBase;

import com.slearn.membermanagement.service.CsvExportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminExportController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminExportControllerTest extends WebMvcTestBase {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CsvExportService csvExportService;

    @Test
    void exportUsers_returnsCsvAttachment() throws Exception {
        when(csvExportService.exportUsers()).thenReturn("\uFEFFID,Name\r\n1,Alice\r\n");

        mockMvc.perform(get("/admin/export/users.csv"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("users.csv")))
                .andExpect(content().contentTypeCompatibleWith("text/csv"))
                .andExpect(content().string(containsString("Alice")));
    }

    @Test
    void exportAllEndpoints_returnOk() throws Exception {
        when(csvExportService.exportPositions()).thenReturn("csv");
        when(csvExportService.exportSkills()).thenReturn("csv");
        when(csvExportService.exportTeams()).thenReturn("csv");
        when(csvExportService.exportProjects()).thenReturn("csv");
        when(csvExportService.exportActivityLogs()).thenReturn("csv");

        mockMvc.perform(get("/admin/export/positions.csv")).andExpect(status().isOk());
        mockMvc.perform(get("/admin/export/skills.csv")).andExpect(status().isOk());
        mockMvc.perform(get("/admin/export/teams.csv")).andExpect(status().isOk());
        mockMvc.perform(get("/admin/export/projects.csv")).andExpect(status().isOk());
        mockMvc.perform(get("/admin/export/activity-logs.csv")).andExpect(status().isOk());
    }
}
