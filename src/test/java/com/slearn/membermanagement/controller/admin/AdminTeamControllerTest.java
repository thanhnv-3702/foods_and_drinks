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
                .andExpect(view().name("admin/teams/list"));
    }

    @Test
    void create_validForm_redirects() throws Exception {
        mockMvc.perform(post("/admin/teams").param("name", "Alpha"))
                .andExpect(redirectedUrl("/admin/teams"));

        verify(teamService).create(any(TeamForm.class));
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
