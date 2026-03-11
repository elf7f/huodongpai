package com.huodongpai.vo.statistics;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class HotEventVO {

    private Long eventId;
    private String title;
    private String categoryName;
    private String location;
    private LocalDateTime startTime;
    private Integer signupCount;
    private Integer approvedCount;
    private Integer checkinCount;
    private BigDecimal checkinRate;
}
