package com.slearn.membermanagement.controller;

import com.slearn.membermanagement.support.WebMvcTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest extends WebMvcTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void list_returnsView() throws Exception {
        when(notificationService.findMyNotifications(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        when(notificationService.countMyUnread()).thenReturn(2L);

        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(view().name("client/notifications/list"));
    }

    @Test
    void markAsRead_redirects() throws Exception {
        mockMvc.perform(post("/notifications/3/read"))
                .andExpect(redirectedUrl("/notifications/3"));

        verify(notificationService).markAsRead(3L);
    }

    @Test
    void delete_redirects() throws Exception {
        mockMvc.perform(post("/notifications/4/delete"))
                .andExpect(redirectedUrl("/notifications"));

        verify(notificationService).deleteMyNotification(4L);
    }
}
