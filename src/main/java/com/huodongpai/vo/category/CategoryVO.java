package com.huodongpai.vo.category;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CategoryVO {

    private Long id;
    private String categoryName;
    private Integer sortNum;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
