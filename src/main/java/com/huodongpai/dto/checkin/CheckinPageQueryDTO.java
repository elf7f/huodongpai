package com.huodongpai.dto.checkin;

import com.huodongpai.common.dto.BasePageQueryDTO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CheckinPageQueryDTO extends BasePageQueryDTO {

    private Long eventId;
    private String keyword;

    @Min(value = 0, message = "签到状态不合法")
    @Max(value = 1, message = "签到状态不合法")
    private Integer checkinStatus;
}
