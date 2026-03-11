package com.huodongpai.vo.statistics;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class DashboardVO {

    private Long totalEvents;
    private Integer totalSignupCount;
    private Integer totalApprovedCount;
    private Integer totalCheckinCount;
    private BigDecimal checkinRate;
    private List<HotEventVO> hotEvents;
}
