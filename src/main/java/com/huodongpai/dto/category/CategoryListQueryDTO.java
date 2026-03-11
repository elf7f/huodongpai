package com.huodongpai.dto.category;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CategoryListQueryDTO {

    private String keyword;

    @Min(value = 0, message = "分类状态不合法")
    @Max(value = 1, message = "分类状态不合法")
    private Integer status;

    private Boolean includeDisabled = false;
}
