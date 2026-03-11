package com.huodongpai.service;

import com.huodongpai.common.result.PageResponse;
import com.huodongpai.dto.event.EventPageQueryDTO;
import com.huodongpai.dto.event.EventSaveDTO;
import com.huodongpai.vo.event.EventDetailVO;
import com.huodongpai.vo.event.EventPageVO;

public interface EventService {

    PageResponse<EventPageVO> getPublicPage(EventPageQueryDTO queryDTO);

    PageResponse<EventPageVO> getManagePage(EventPageQueryDTO queryDTO);

    EventDetailVO getDetail(Long eventId, Long currentUserId, boolean isAdmin);

    Long create(EventSaveDTO saveDTO, Long operatorId);

    void update(EventSaveDTO saveDTO);

    void delete(Long eventId);

    void publish(Long eventId);

    void cancel(Long eventId);
}
