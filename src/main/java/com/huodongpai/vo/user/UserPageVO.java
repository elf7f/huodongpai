package com.huodongpai.vo.user;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserPageVO {

    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String role;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
