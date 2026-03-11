package com.huodongpai.dto.user;

import com.huodongpai.common.dto.BasePageQueryDTO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserPageQueryDTO extends BasePageQueryDTO {

    private String keyword;
    private String role;

    @Min(value = 0, message = "用户状态不合法")
    @Max(value = 1, message = "用户状态不合法")
    private Integer status;
}
