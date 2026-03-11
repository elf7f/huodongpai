package com.huodongpai.dto.event;

import com.huodongpai.common.dto.BasePageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EventPageQueryDTO extends BasePageQueryDTO {

    private String title;
    private Long categoryId;
    private String baseStatus;
    private String runtimeStatus;
}
