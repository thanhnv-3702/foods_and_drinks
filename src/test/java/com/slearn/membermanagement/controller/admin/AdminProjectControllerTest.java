package com.slearn.membermanagement.controller.admin;

import com.slearn.membermanagement.dto.ProjectForm;
import com.slearn.membermanagement.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AdminProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Test
    void list_returnsListView() throws Exception {
        when(projectService.findAll(isNull(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/admin/projects"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/projects/list"));
    }

    @Test
    void create_validForm_redirects() throws Exception {
        mockMvc.perform(post("/admin/projects")
                        .param("name", "Portal")
                        .param("teamId", "1"))
                .andExpect(redirectedUrl("/admin/projects"));

        verify(projectService).create(any(ProjectForm.class));
    }

    @Test
    void create_invalidDateRange_returnsForm() throws Exception {
        mockMvc.perform(post("/admin/projects")
                        .param("name", "Portal")
                        .param("teamId", "1")
                        .param("startDate", "2025-12-01")
                        .param("endDate", "2025-01-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/projects/form"));
    }

    @Test
    void delete_redirects() throws Exception {
        mockMvc.perform(post("/admin/projects/4/delete"))
                .andExpect(redirectedUrl("/admin/projects"));

        verify(projectService).delete(4L);
    }

    @Test
    void update_validForm_redirects() throws Exception {
        mockMvc.perform(post("/admin/projects/2")
                        .param("name", "API")
                        .param("teamId", "1"))
                .andExpect(redirectedUrl("/admin/projects"));

        verify(projectService).update(eq(2L), any(ProjectForm.class));
    }
}
