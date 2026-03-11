package com.huodongpai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.huodongpai.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("event_category")
@EqualsAndHashCode(callSuper = true)
public class EventCategory extends BaseEntity {

    private String categoryName;
    private Integer sortNum;
    private Integer status;
}
