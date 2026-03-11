package com.huodongpai.dto.event;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class EventSaveDTO {

    private Long id;

    @NotBlank(message = "活动标题不能为空")
    private String title;

    @NotNull(message = "活动分类不能为空")
    private Long categoryId;

    private String coverUrl;

    @NotBlank(message = "活动地点不能为空")
    private String location;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    @NotNull(message = "报名截止时间不能为空")
    private LocalDateTime signupDeadline;

    @NotNull(message = "最大报名人数不能为空")
    @Min(value = 1, message = "最大报名人数必须大于0")
    private Integer maxParticipants;

    @NotNull(message = "是否需要审核不能为空")
    @Min(value = 0, message = "审核状态不合法")
    @Max(value = 1, message = "审核状态不合法")
    private Integer needAudit;

    @NotBlank(message = "活动详情不能为空")
    private String description;
}
