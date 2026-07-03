package com.slearn.membermanagement.controller;

import com.slearn.membermanagement.dto.ProfileView;
import com.slearn.membermanagement.service.ClientTeamService;
import com.slearn.membermanagement.service.ProfileService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(ClientMemberController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClientMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientTeamService clientTeamService;

    @MockBean
    private ProfileService profileService;

    @Test
    void members_returnsView() throws Exception {
        when(clientTeamService.getTeam(1L)).thenReturn(TestEntityFactory.team(1L));
        when(clientTeamService.getMembers(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/teams/1/members"))
                .andExpect(status().isOk())
                .andExpect(view().name("client/teams/members"));
    }

    @Test
    void memberProfile_returnsView() throws Exception {
        when(profileService.getProfile(2L)).thenReturn(
                ProfileView.builder().id(2L).name("Member").build());

        mockMvc.perform(get("/members/2"))
                .andExpect(status().isOk())
                .andExpect(view().name("client/members/profile"));
    }
}
