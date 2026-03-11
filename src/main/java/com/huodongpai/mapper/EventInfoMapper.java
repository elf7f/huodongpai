package com.huodongpai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huodongpai.dto.event.EventPageQueryDTO;
import com.huodongpai.entity.EventInfo;
import com.huodongpai.vo.event.EventDetailVO;
import com.huodongpai.vo.event.EventPageVO;
import org.apache.ibatis.annotations.Param;

public interface EventInfoMapper extends BaseMapper<EventInfo> {

    IPage<EventPageVO> selectPublicPage(Page<EventPageVO> page, @Param("query") EventPageQueryDTO queryDTO);

    IPage<EventPageVO> selectManagePage(Page<EventPageVO> page, @Param("query") EventPageQueryDTO queryDTO);

    EventDetailVO selectDetailById(@Param("eventId") Long eventId);
}
