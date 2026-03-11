package com.huodongpai.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;
import lombok.Data;

@Data
public class PageResponse<T> {

    private Long pageNum;
    private Long pageSize;
    private Long total;
    private List<T> list;

    public static <T> PageResponse<T> of(IPage<T> page) {
        PageResponse<T> response = new PageResponse<>();
        response.setPageNum(page.getCurrent());
        response.setPageSize(page.getSize());
        response.setTotal(page.getTotal());
        response.setList(page.getRecords());
        return response;
    }
}
