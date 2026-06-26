package com.slearn.membermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionForm {

    private Long id;

    @NotBlank(message = "Tên vị trí không được để trống")
    @Size(max = 150, message = "Tên vị trí tối đa 150 ký tự")
    private String name;

    @Size(max = 50, message = "Tên viết tắt tối đa 50 ký tự")
    private String abbreviation;
}
