package com.huodongpai.dto.signup;

import com.huodongpai.common.dto.BasePageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MySignupPageQueryDTO extends BasePageQueryDTO {

    private String status;
    private String keyword;
}
