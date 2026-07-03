package com.slearn.membermanagement.controller.admin;

import com.slearn.membermanagement.support.WebMvcTestBase;

import com.slearn.membermanagement.dto.UserForm;
import com.slearn.membermanagement.entity.Role;
import com.slearn.membermanagement.service.UserService;
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

@WebMvcTest(AdminUserController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminUserControllerTest extends WebMvcTestBase {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void createForm_returnsFormView() throws Exception {
        when(userService.findAllTeams()).thenReturn(List.of());
        when(userService.findAllPositions()).thenReturn(List.of());

        mockMvc.perform(get("/admin/users/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/form"));
    }

    @Test
    void editForm_returnsFormView() throws Exception {
        when(userService.getFormById(1L)).thenReturn(UserForm.builder().id(1L).role(Role.USER).build());
        when(userService.findAllTeams()).thenReturn(List.of());
        when(userService.findAllPositions()).thenReturn(List.of());

        mockMvc.perform(get("/admin/users/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/form"));
    }

    @Test
    void create_shortPassword_returnsForm() throws Exception {
        when(userService.findAllTeams()).thenReturn(List.of());
        when(userService.findAllPositions()).thenReturn(List.of());

        mockMvc.perform(post("/admin/users")
                        .param("name", "Alice")
                        .param("email", "alice@test.local")
                        .param("password", "123")
                        .param("role", "USER"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/form"));
    }

    @Test
    void update_duplicateEmail_returnsForm() throws Exception {
        when(userService.emailExists("dup@test.local", 3L)).thenReturn(true);
        when(userService.findAllTeams()).thenReturn(List.of());
        when(userService.findAllPositions()).thenReturn(List.of());

        mockMvc.perform(post("/admin/users/3")
                        .param("name", "Carol")
                        .param("email", "dup@test.local")
                        .param("role", "USER"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/form"));
    }

    @Test
    void update_shortPassword_returnsForm() throws Exception {
        when(userService.findAllTeams()).thenReturn(List.of());
        when(userService.findAllPositions()).thenReturn(List.of());

        mockMvc.perform(post("/admin/users/3")
                        .param("name", "Carol")
                        .param("email", "carol@test.local")
                        .param("password", "123")
                        .param("role", "USER"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/form"));
    }

    @Test
    void list_returnsListView() throws Exception {
        when(userService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/list"));
    }

    @Test
    void create_validForm_redirects() throws Exception {
        mockMvc.perform(post("/admin/users")
                        .param("name", "Alice")
                        .param("email", "alice@test.local")
                        .param("password", "secret1")
                        .param("role", "USER"))
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService).create(any(UserForm.class));
    }

    @Test
    void create_missingPassword_returnsForm() throws Exception {
        mockMvc.perform(post("/admin/users")
                        .param("name", "Alice")
                        .param("email", "alice@test.local")
                        .param("role", "USER"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/form"));
    }

    @Test
    void create_duplicateEmail_returnsForm() throws Exception {
        when(userService.emailExists("dup@test.local", null)).thenReturn(true);

        mockMvc.perform(post("/admin/users")
                        .param("name", "Bob")
                        .param("email", "dup@test.local")
                        .param("password", "secret1")
                        .param("role", "USER"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/form"));
    }

    @Test
    void delete_success_redirects() throws Exception {
        when(userService.delete(5L, null)).thenReturn(null);

        mockMvc.perform(post("/admin/users/5/delete"))
                .andExpect(redirectedUrl("/admin/users"))
                .andExpect(flash().attribute("successMessage", "Đã xóa người dùng."));
    }

    @Test
    void delete_error_showsFlash() throws Exception {
        when(userService.delete(6L, null)).thenReturn("Lỗi xóa");

        mockMvc.perform(post("/admin/users/6/delete"))
                .andExpect(flash().attribute("errorMessage", "Lỗi xóa"));
    }

    @Test
    void update_validForm_redirects() throws Exception {
        mockMvc.perform(post("/admin/users/3")
                        .param("name", "Carol")
                        .param("email", "carol@test.local")
                        .param("role", "ADMIN"))
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService).update(eq(3L), any(UserForm.class));
    }
}
