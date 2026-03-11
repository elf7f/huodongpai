package com.huodongpai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.huodongpai.common.entity.BaseCreateEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("event_signup_log")
@EqualsAndHashCode(callSuper = true)
public class EventSignupLog extends BaseCreateEntity {

    private Long signupId;
    private Long eventId;
    private Long userId;
    private String operationType;
    private String targetStatus;
    private Long operatorId;
    private String remark;
    private LocalDateTime operationTime;
}
