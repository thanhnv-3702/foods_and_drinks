package com.slearn.membermanagement.controller.admin;

import com.slearn.membermanagement.dto.ImportResult;
import com.slearn.membermanagement.service.CsvImportService;
import com.slearn.membermanagement.support.WebMvcTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AdminImportController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminImportControllerTest extends WebMvcTestBase {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CsvImportService csvImportService;

    @Test
    void page_returnsImportView() throws Exception {
        mockMvc.perform(get("/admin/import"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/import/index"));
    }

    @Test
    void importEntity_emptyFile_showsError() throws Exception {
        mockMvc.perform(multipart("/admin/import/positions")
                        .file(new MockMultipartFile("file", new byte[0])))
                .andExpect(redirectedUrl("/admin/import"))
                .andExpect(flash().attribute("errorMessage", "Please select a CSV file."));
    }

    @Test
    void importEntity_invalidEntity_showsError() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "x.csv", "text/csv", "a".getBytes());

        mockMvc.perform(multipart("/admin/import/unknown").file(file))
                .andExpect(redirectedUrl("/admin/import"))
                .andExpect(flash().attribute("errorMessage", "Invalid import target: unknown"));
    }

    @Test
    void importEntity_readError_showsError() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "x.csv", "text/csv", "a".getBytes());
        when(csvImportService.importPositions(any())).thenThrow(new RuntimeException("bad format"));

        mockMvc.perform(multipart("/admin/import/positions").file(file))
                .andExpect(redirectedUrl("/admin/import"))
                .andExpect(flash().attribute("errorMessage", "Failed to read file: bad format"));
    }

    @Test
    void importEntity_allTypes_storeResult() throws Exception {
        ImportResult result = new ImportResult();
        result.incrementSuccess();
        MockMultipartFile file = new MockMultipartFile("file", "data.csv", "text/csv", "id,name".getBytes());

        when(csvImportService.importPositions(any())).thenReturn(result);
        when(csvImportService.importSkills(any())).thenReturn(result);
        when(csvImportService.importTeams(any())).thenReturn(result);
        when(csvImportService.importUsers(any())).thenReturn(result);
        when(csvImportService.importProjects(any())).thenReturn(result);

        mockMvc.perform(multipart("/admin/import/positions").file(file))
                .andExpect(redirectedUrl("/admin/import"))
                .andExpect(flash().attribute("importEntity", "positions"));
        mockMvc.perform(multipart("/admin/import/skills").file(file))
                .andExpect(flash().attribute("importEntity", "skills"));
        mockMvc.perform(multipart("/admin/import/teams").file(file))
                .andExpect(flash().attribute("importEntity", "teams"));
        mockMvc.perform(multipart("/admin/import/users").file(file))
                .andExpect(flash().attribute("importEntity", "users"));
        mockMvc.perform(multipart("/admin/import/projects").file(file))
                .andExpect(flash().attribute("importEntity", "projects"));
    }
}
