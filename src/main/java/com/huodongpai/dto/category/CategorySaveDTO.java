package com.huodongpai.dto.category;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategorySaveDTO {

    private Long id;

    @NotBlank(message = "分类名称不能为空")
    private String categoryName;

    @NotNull(message = "排序值不能为空")
    private Integer sortNum;

    @NotNull(message = "分类状态不能为空")
    @Min(value = 0, message = "分类状态不合法")
    @Max(value = 1, message = "分类状态不合法")
    private Integer status;
}
