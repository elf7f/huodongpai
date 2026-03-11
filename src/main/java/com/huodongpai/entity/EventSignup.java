package com.huodongpai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.huodongpai.common.entity.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("event_signup")
@EqualsAndHashCode(callSuper = true)
public class EventSignup extends BaseEntity {

    private Long eventId;
    private Long userId;
    private String status;
    private String remark;
    private LocalDateTime signupTime;
    private LocalDateTime auditTime;
    private Long auditBy;
    private LocalDateTime cancelTime;
}
