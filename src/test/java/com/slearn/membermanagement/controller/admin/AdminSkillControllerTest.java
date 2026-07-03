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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
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
                .andExpect(view().name("admin/skills/list"))
                .andExpect(model().attribute("pageTitle", "Skills"))
                .andExpect(model().attribute("activeMenu", "skills"));
    }

    @Test
    void createForm_returnsFormView() throws Exception {
        when(skillService.findAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/admin/skills/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/skills/form"))
                .andExpect(model().attributeExists("skillForm"))
                .andExpect(model().attribute("pageTitle", "Tạo Skill"));
    }

    @Test
    void create_validForm_redirects() throws Exception {
        mockMvc.perform(post("/admin/skills")
                        .param("name", "Java")
                        .param("userId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/skills"))
                .andExpect(flash().attribute("successMessage", "Đã tạo kỹ năng thành công."));

        verify(skillService).create(any(SkillForm.class));
    }

    @Test
    void create_invalidForm_returnsForm() throws Exception {
        when(skillService.findAllUsers()).thenReturn(List.of());

        mockMvc.perform(post("/admin/skills").param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/skills/form"))
                .andExpect(model().attribute("pageTitle", "Tạo Skill"));
    }

    @Test
    void create_missingUserId_returnsForm() throws Exception {
        when(skillService.findAllUsers()).thenReturn(List.of());

        mockMvc.perform(post("/admin/skills").param("name", "Python"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/skills/form"));
    }

    @Test
    void editForm_loadsForm() throws Exception {
        when(skillService.getFormById(1L)).thenReturn(
                SkillForm.builder().id(1L).name("Java").userId(2L).build());
        when(skillService.findAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/admin/skills/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/skills/form"))
                .andExpect(model().attribute("pageTitle", "Sửa Skill"));
    }

    @Test
    void update_validForm_redirects() throws Exception {
        mockMvc.perform(post("/admin/skills/2")
                        .param("name", "Go")
                        .param("userId", "1"))
                .andExpect(redirectedUrl("/admin/skills"))
                .andExpect(flash().attribute("successMessage", "Đã cập nhật kỹ năng thành công."));

        verify(skillService).update(eq(2L), any(SkillForm.class));
    }

    @Test
    void update_invalidForm_returnsForm() throws Exception {
        when(skillService.findAllUsers()).thenReturn(List.of());

        mockMvc.perform(post("/admin/skills/2").param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/skills/form"))
                .andExpect(model().attribute("pageTitle", "Sửa Skill"));
    }

    @Test
    void delete_redirects() throws Exception {
        mockMvc.perform(post("/admin/skills/3/delete"))
                .andExpect(redirectedUrl("/admin/skills"))
                .andExpect(flash().attribute("successMessage", "Đã xóa kỹ năng."));

        verify(skillService).delete(3L);
    }
}
