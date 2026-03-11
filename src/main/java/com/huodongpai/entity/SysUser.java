package com.huodongpai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.huodongpai.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("sys_user")
@EqualsAndHashCode(callSuper = true)
public class SysUser extends BaseEntity {

    private String username;
    private String password;
    private String realName;
    private String phone;
    private String role;
    private Integer status;
}
