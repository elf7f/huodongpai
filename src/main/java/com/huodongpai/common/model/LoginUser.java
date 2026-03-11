package com.huodongpai.common.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginUser {

    private Long id;
    private String username;
    private String realName;
    private String role;
    private Integer status;
}
