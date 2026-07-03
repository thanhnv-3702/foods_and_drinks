package com.slearn.membermanagement.controller;

import com.slearn.membermanagement.service.ClientTeamService;
import com.slearn.membermanagement.support.TestEntityFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(ClientTeamController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClientTeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientTeamService clientTeamService;

    @Test
    void list_returnsView() throws Exception {
        when(clientTeamService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));
        when(clientTeamService.memberCountByTeam()).thenReturn(Map.of());

        mockMvc.perform(get("/teams"))
                .andExpect(status().isOk())
                .andExpect(view().name("client/teams/list"));
    }

    @Test
    void detail_returnsView() throws Exception {
        var team = TestEntityFactory.team(1L);
        when(clientTeamService.getTeam(1L)).thenReturn(team);
        when(clientTeamService.getMembers(1L)).thenReturn(List.of());
        when(clientTeamService.getProjects(1L)).thenReturn(List.of());

        mockMvc.perform(get("/teams/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("client/teams/detail"));
    }
}
