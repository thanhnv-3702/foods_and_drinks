package com.slearn.membermanagement.controller.admin;

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
class AdminExportControllerTest {

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
}
