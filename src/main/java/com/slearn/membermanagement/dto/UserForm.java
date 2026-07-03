package com.slearn.membermanagement.dto;

import com.slearn.membermanagement.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserForm {

    private Long id;

    @NotBlank(message = "{validation.name.required}")
    @Size(max = 150, message = "{validation.name.max}")
    private String name;

    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    @Size(max = 150, message = "{validation.email.max}")
    private String email;

    /**
     * Bắt buộc khi tạo mới; khi sửa để trống nghĩa là giữ nguyên mật khẩu cũ.
     * Validate độ dài thực hiện thủ công trong controller để hỗ trợ ngữ nghĩa trên.
     */
    private String password;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthday;

    @NotNull(message = "{validation.role.required}")
    private Role role;

    private Long teamId;

    private Long positionId;
}
