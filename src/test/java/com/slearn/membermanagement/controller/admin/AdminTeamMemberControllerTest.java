package com.slearn.membermanagement.controller.admin;

import com.slearn.membermanagement.support.WebMvcTestBase;

import com.slearn.membermanagement.service.TeamMemberService;
import com.slearn.membermanagement.support.TestEntityFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AdminTeamMemberController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminTeamMemberControllerTest extends WebMvcTestBase {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeamMemberService teamMemberService;

    @Test
    void members_returnsView() throws Exception {
        var team = TestEntityFactory.team(1L);
        when(teamMemberService.getTeam(1L)).thenReturn(team);
        when(teamMemberService.getMembers(1L)).thenReturn(List.of());
        when(teamMemberService.getCandidates(1L)).thenReturn(List.of());
        when(teamMemberService.getHistory(1L)).thenReturn(List.of());

        mockMvc.perform(get("/admin/teams/1/members"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/teams/members"));
    }

    @Test
    void addMember_redirects() throws Exception {
        mockMvc.perform(post("/admin/teams/1/members/add").param("userId", "2"))
                .andExpect(redirectedUrl("/admin/teams/1/members"));

        verify(teamMemberService).addOrMoveMember(1L, 2L);
    }

    @Test
    void removeMember_redirects() throws Exception {
        mockMvc.perform(post("/admin/teams/1/members/3/remove"))
                .andExpect(redirectedUrl("/admin/teams/1/members"));

        verify(teamMemberService).removeMember(1L, 3L);
    }
}
