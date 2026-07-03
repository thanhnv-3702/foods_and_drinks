package com.slearn.membermanagement.controller.admin;

import com.slearn.membermanagement.support.WebMvcTestBase;

import com.slearn.membermanagement.dto.PositionForm;
import com.slearn.membermanagement.service.PositionService;
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

@WebMvcTest(AdminPositionController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminPositionControllerTest extends WebMvcTestBase {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PositionService positionService;

    @Test
    void list_returnsListView() throws Exception {
        when(positionService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/admin/positions"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/positions/list"));
    }

    @Test
    void create_validForm_redirects() throws Exception {
        mockMvc.perform(post("/admin/positions")
                        .param("name", "Developer")
                        .param("abbreviation", "DEV"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/positions"));

        verify(positionService).create(any(PositionForm.class));
    }

    @Test
    void create_invalidForm_returnsForm() throws Exception {
        mockMvc.perform(post("/admin/positions")
                        .param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/positions/form"));
    }

    @Test
    void delete_redirectsToList() throws Exception {
        mockMvc.perform(post("/admin/positions/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/positions"));

        verify(positionService).delete(1L);
    }

    @Test
    void editForm_loadsForm() throws Exception {
        when(positionService.getFormById(1L)).thenReturn(
                PositionForm.builder().id(1L).name("Dev").abbreviation("DV").build());

        mockMvc.perform(get("/admin/positions/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/positions/form"));
    }

    @Test
    void update_validForm_redirects() throws Exception {
        mockMvc.perform(post("/admin/positions/2")
                        .param("name", "Lead")
                        .param("abbreviation", "LD"))
                .andExpect(redirectedUrl("/admin/positions"));

        verify(positionService).update(eq(2L), any(PositionForm.class));
    }
}
