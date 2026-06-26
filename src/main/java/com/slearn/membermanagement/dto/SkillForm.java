package com.slearn.membermanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillForm {

    private Long id;

    @NotBlank(message = "Tên kỹ năng không được để trống")
    @Size(max = 150, message = "Tên kỹ năng tối đa 150 ký tự")
    private String name;

    @Size(max = 50, message = "Mức độ tối đa 50 ký tự")
    private String level;

    @Min(value = 0, message = "Số năm sử dụng không hợp lệ")
    private Integer usedYearNumber;

    @NotNull(message = "Phải chọn người sở hữu kỹ năng")
    private Long userId;
}
