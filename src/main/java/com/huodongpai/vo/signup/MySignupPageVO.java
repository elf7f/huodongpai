package com.huodongpai.vo.signup;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class MySignupPageVO {

    private Long signupId;
    private Long eventId;
    private String eventTitle;
    private String coverUrl;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime signupDeadline;
    private Integer needAudit;
    private String status;
    private LocalDateTime signupTime;
    private LocalDateTime auditTime;
    private LocalDateTime cancelTime;
    private Integer checkinStatus;
    private LocalDateTime checkinTime;
    private String runtimeStatus;
}
