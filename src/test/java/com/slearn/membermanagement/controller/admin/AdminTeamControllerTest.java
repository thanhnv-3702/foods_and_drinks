package com.slearn.membermanagement.controller.admin;

import com.slearn.membermanagement.dto.TeamForm;
import com.slearn.membermanagement.service.TeamService;
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

@WebMvcTest(AdminTeamController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminTeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeamService teamService;

    @Test
    void list_returnsListView() throws Exception {
        when(teamService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/admin/teams"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/teams/list"))
                .andExpect(model().attribute("pageTitle", "Teams"))
                .andExpect(model().attribute("activeMenu", "teams"));
    }

    @Test
    void createForm_returnsFormView() throws Exception {
        when(teamService.findAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/admin/teams/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/teams/form"))
                .andExpect(model().attributeExists("teamForm"))
                .andExpect(model().attribute("pageTitle", "Tạo Team"));
    }

    @Test
    void create_validForm_redirects() throws Exception {
        mockMvc.perform(post("/admin/teams").param("name", "Alpha"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/teams"))
                .andExpect(flash().attribute("successMessage", "Đã tạo team thành công."));

        verify(teamService).create(any(TeamForm.class));
    }

    @Test
    void create_invalidForm_returnsForm() throws Exception {
        when(teamService.findAllUsers()).thenReturn(List.of());

        mockMvc.perform(post("/admin/teams").param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/teams/form"))
                .andExpect(model().attribute("pageTitle", "Tạo Team"));
    }

    @Test
    void editForm_loadsForm() throws Exception {
        when(teamService.getFormById(1L)).thenReturn(
                TeamForm.builder().id(1L).name("Alpha").description("Team A").build());
        when(teamService.findAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/admin/teams/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/teams/form"))
                .andExpect(model().attribute("pageTitle", "Sửa Team"));
    }

    @Test
    void update_validForm_redirects() throws Exception {
        mockMvc.perform(post("/admin/teams/2")
                        .param("name", "Beta")
                        .param("description", "Updated"))
                .andExpect(redirectedUrl("/admin/teams"))
                .andExpect(flash().attribute("successMessage", "Đã cập nhật team thành công."));

        verify(teamService).update(eq(2L), any(TeamForm.class));
    }

    @Test
    void update_invalidForm_returnsForm() throws Exception {
        when(teamService.findAllUsers()).thenReturn(List.of());

        mockMvc.perform(post("/admin/teams/2").param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/teams/form"))
                .andExpect(model().attribute("pageTitle", "Sửa Team"));
    }

    @Test
    void delete_success_showsSuccessFlash() throws Exception {
        when(teamService.delete(1L)).thenReturn(null);

        mockMvc.perform(post("/admin/teams/1/delete"))
                .andExpect(redirectedUrl("/admin/teams"))
                .andExpect(flash().attribute("successMessage", "Đã xóa team."));
    }

    @Test
    void delete_withMembers_showsErrorFlash() throws Exception {
        when(teamService.delete(2L)).thenReturn("Không thể xóa team");

        mockMvc.perform(post("/admin/teams/2/delete"))
                .andExpect(redirectedUrl("/admin/teams"))
                .andExpect(flash().attribute("errorMessage", "Không thể xóa team"));
    }
}
