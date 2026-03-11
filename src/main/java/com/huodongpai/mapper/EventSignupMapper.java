package com.huodongpai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huodongpai.dto.signup.MySignupPageQueryDTO;
import com.huodongpai.dto.signup.SignupPageQueryDTO;
import com.huodongpai.entity.EventSignup;
import com.huodongpai.vo.signup.MySignupPageVO;
import com.huodongpai.vo.signup.SignupAdminPageVO;
import org.apache.ibatis.annotations.Param;

public interface EventSignupMapper extends BaseMapper<EventSignup> {

    IPage<MySignupPageVO> selectMyPage(Page<MySignupPageVO> page,
                                       @Param("userId") Long userId,
                                       @Param("query") MySignupPageQueryDTO queryDTO);

    IPage<SignupAdminPageVO> selectAdminPage(Page<SignupAdminPageVO> page,
                                             @Param("query") SignupPageQueryDTO queryDTO);
}
