package com.huodongpai.dto.signup;

import com.huodongpai.common.dto.BasePageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SignupPageQueryDTO extends BasePageQueryDTO {

    private Long eventId;
    private String status;
    private String keyword;
}
