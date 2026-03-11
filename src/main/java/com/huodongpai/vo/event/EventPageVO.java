package com.huodongpai.vo.event;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class EventPageVO {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String coverUrl;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime signupDeadline;
    private Integer maxParticipants;
    private Integer needAudit;
    private String baseStatus;
    private String runtimeStatus;
    private Integer signupCount;
    private Integer approvedCount;
    private Integer checkinCount;
    private Integer remainingSlots;
}
