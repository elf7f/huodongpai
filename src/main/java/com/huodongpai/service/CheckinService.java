package com.huodongpai.service;

import com.huodongpai.common.result.PageResponse;
import com.huodongpai.dto.checkin.CheckinPageQueryDTO;
import com.huodongpai.vo.checkin.CheckinPageVO;

public interface CheckinService {

    PageResponse<CheckinPageVO> getPage(CheckinPageQueryDTO queryDTO);

    void doCheckin(Long signupId);
}
