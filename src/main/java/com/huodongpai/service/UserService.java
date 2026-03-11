package com.huodongpai.service;

import com.huodongpai.common.result.PageResponse;
import com.huodongpai.dto.user.UserAddDTO;
import com.huodongpai.dto.user.UserPageQueryDTO;
import com.huodongpai.dto.user.UserStatusUpdateDTO;
import com.huodongpai.dto.user.UserUpdateDTO;
import com.huodongpai.vo.user.UserPageVO;

public interface UserService {

    PageResponse<UserPageVO> getPage(UserPageQueryDTO queryDTO);

    Long add(UserAddDTO addDTO, Long operatorId);

    void update(UserUpdateDTO updateDTO, Long operatorId);

    void updateStatus(Long id, UserStatusUpdateDTO updateDTO, Long operatorId);
}
