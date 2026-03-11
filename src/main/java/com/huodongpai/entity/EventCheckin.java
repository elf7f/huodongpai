package com.huodongpai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.huodongpai.common.entity.BaseCreateEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("event_checkin")
@EqualsAndHashCode(callSuper = true)
public class EventCheckin extends BaseCreateEntity {

    private Long eventId;
    private Long signupId;
    private Long userId;
    private Integer checkinStatus;
    private LocalDateTime checkinTime;
}
