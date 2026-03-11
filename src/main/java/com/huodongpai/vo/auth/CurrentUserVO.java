package com.huodongpai.vo.auth;

import lombok.Data;

@Data
public class CurrentUserVO {

    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String role;
    private Integer status;
}
