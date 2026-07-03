package com.slearn.membermanagement.controller.admin;

import com.slearn.membermanagement.dto.SkillForm;
import com.slearn.membermanagement.service.SkillService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AdminSkillController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminSkillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SkillService skillService;

    @Test
    void list_returnsListView() throws Exception {
        when(skillService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/admin/skills"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/skills/list"));
    }

    @Test
    void create_validForm_redirects() throws Exception {
        mockMvc.perform(post("/admin/skills")
                        .param("name", "Java")
                        .param("userId", "1"))
                .andExpect(redirectedUrl("/admin/skills"));

        verify(skillService).create(any(SkillForm.class));
    }

    @Test
    void create_invalidForm_returnsForm() throws Exception {
        mockMvc.perform(post("/admin/skills").param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/skills/form"));
    }

    @Test
    void delete_redirects() throws Exception {
        mockMvc.perform(post("/admin/skills/3/delete"))
                .andExpect(redirectedUrl("/admin/skills"));

        verify(skillService).delete(3L);
    }

    @Test
    void update_validForm_redirects() throws Exception {
        mockMvc.perform(post("/admin/skills/2")
                        .param("name", "Go")
                        .param("userId", "1"))
                .andExpect(redirectedUrl("/admin/skills"));

        verify(skillService).update(eq(2L), any(SkillForm.class));
    }
}
