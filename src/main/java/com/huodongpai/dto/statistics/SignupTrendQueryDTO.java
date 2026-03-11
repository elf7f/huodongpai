package com.huodongpai.dto.statistics;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class SignupTrendQueryDTO {

    @Min(value = 1, message = "统计天数不能小于1")
    @Max(value = 90, message = "统计天数不能超过90")
    private Integer days = 7;
}
