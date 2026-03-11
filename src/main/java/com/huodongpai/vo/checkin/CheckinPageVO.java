package com.huodongpai.vo.checkin;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CheckinPageVO {

    private Long signupId;
    private Long eventId;
    private String eventTitle;
    private Long userId;
    private String username;
    private String realName;
    private String phone;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime signupTime;
    private LocalDateTime auditTime;
    private Integer checkinStatus;
    private LocalDateTime checkinTime;
}
