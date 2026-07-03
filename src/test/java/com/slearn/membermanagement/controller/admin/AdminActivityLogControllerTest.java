package com.slearn.membermanagement.controller.admin;

import com.slearn.membermanagement.service.ActivityLogService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AdminActivityLogController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminActivityLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActivityLogService activityLogService;

    @Test
    void list_returnsListView() throws Exception {
        when(activityLogService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/admin/activity-logs"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/activity-logs/list"));
    }

    @Test
    void delete_redirects() throws Exception {
        mockMvc.perform(post("/admin/activity-logs/7/delete"))
                .andExpect(redirectedUrl("/admin/activity-logs"));

        verify(activityLogService).delete(7L);
    }

    @Test
    void clear_redirects() throws Exception {
        mockMvc.perform(post("/admin/activity-logs/clear"))
                .andExpect(redirectedUrl("/admin/activity-logs"));

        verify(activityLogService).deleteAll();
    }
}
