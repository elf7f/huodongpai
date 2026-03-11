package com.huodongpai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.huodongpai.common.entity.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("event_info")
@EqualsAndHashCode(callSuper = true)
public class EventInfo extends BaseEntity {

    private String title;
    private Long categoryId;
    private String coverUrl;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime signupDeadline;
    private Integer maxParticipants;
    private Integer needAudit;
    private String status;
    private String description;
    private Integer signupCount;
    private Integer approvedCount;
    private Integer checkinCount;
    private Long createBy;
    @Version
    private Integer version;
}
