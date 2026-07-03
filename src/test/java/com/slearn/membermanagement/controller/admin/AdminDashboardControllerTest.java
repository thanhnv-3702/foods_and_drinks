package com.slearn.membermanagement.controller.admin;

import com.slearn.membermanagement.support.WebMvcTestBase;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AdminDashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminDashboardControllerTest extends WebMvcTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void dashboard_returnsView() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attribute("activeMenu", "dashboard"));
    }
}
