package com.huodongpai.dto.statistics;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class HotEventQueryDTO {

    @Min(value = 1, message = "榜单数量不能小于1")
    @Max(value = 20, message = "榜单数量不能超过20")
    private Integer limit = 10;
}
