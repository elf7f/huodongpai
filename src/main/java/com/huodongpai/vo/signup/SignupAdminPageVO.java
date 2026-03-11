package com.huodongpai.vo.signup;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SignupAdminPageVO {

    private Long signupId;
    private Long eventId;
    private String eventTitle;
    private Long userId;
    private String username;
    private String realName;
    private String phone;
    private String status;
    private String remark;
    private LocalDateTime signupTime;
    private LocalDateTime auditTime;
    private LocalDateTime cancelTime;
    private Long auditBy;
    private String auditByName;
    private Integer checkinStatus;
    private LocalDateTime checkinTime;
}
