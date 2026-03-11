package com.huodongpai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huodongpai.dto.checkin.CheckinPageQueryDTO;
import com.huodongpai.entity.EventCheckin;
import com.huodongpai.vo.checkin.CheckinPageVO;
import org.apache.ibatis.annotations.Param;

public interface EventCheckinMapper extends BaseMapper<EventCheckin> {

    IPage<CheckinPageVO> selectPage(Page<CheckinPageVO> page, @Param("query") CheckinPageQueryDTO queryDTO);
}
