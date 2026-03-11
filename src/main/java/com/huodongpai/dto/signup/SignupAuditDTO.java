package com.huodongpai.dto.signup;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupAuditDTO {

    @Size(max = 255, message = "备注长度不能超过255")
    private String remark;
}
