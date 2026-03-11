package com.huodongpai.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huodongpai.common.enums.CheckinStatusEnum;
import com.huodongpai.common.enums.EventBaseStatusEnum;
import com.huodongpai.common.enums.SignupStatusEnum;
import com.huodongpai.common.result.PageResponse;
import com.huodongpai.dto.checkin.CheckinPageQueryDTO;
import com.huodongpai.entity.EventCheckin;
import com.huodongpai.entity.EventInfo;
import com.huodongpai.entity.EventSignup;
import com.huodongpai.exception.BusinessException;
import com.huodongpai.mapper.EventCheckinMapper;
import com.huodongpai.mapper.EventInfoMapper;
import com.huodongpai.mapper.EventSignupMapper;
import com.huodongpai.service.CheckinService;
import com.huodongpai.vo.checkin.CheckinPageVO;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckinServiceImpl implements CheckinService {

    private final EventCheckinMapper eventCheckinMapper;
    private final EventSignupMapper eventSignupMapper;
    private final EventInfoMapper eventInfoMapper;

    public CheckinServiceImpl(EventCheckinMapper eventCheckinMapper,
                              EventSignupMapper eventSignupMapper,
                              EventInfoMapper eventInfoMapper) {
        this.eventCheckinMapper = eventCheckinMapper;
        this.eventSignupMapper = eventSignupMapper;
        this.eventInfoMapper = eventInfoMapper;
    }

    @Override
    public PageResponse<CheckinPageVO> getPage(CheckinPageQueryDTO queryDTO) {
        IPage<CheckinPageVO> page = eventCheckinMapper.selectPage(new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()), queryDTO);
        return PageResponse.of(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doCheckin(Long signupId) {
        EventSignup signup = eventSignupMapper.selectById(signupId);
        if (signup == null) {
            throw new BusinessException("报名记录不存在");
        }
        if (!SignupStatusEnum.APPROVED.getCode().equals(signup.getStatus())) {
            throw new BusinessException("仅审核通过的报名记录可签到");
        }
        EventInfo eventInfo = eventInfoMapper.selectById(signup.getEventId());
        if (eventInfo == null) {
            throw new BusinessException("活动不存在");
        }
        if (EventBaseStatusEnum.CANCELLED.getCode().equals(eventInfo.getStatus())) {
            throw new BusinessException("活动已取消，无法签到");
        }
        if (LocalDateTime.now().isBefore(eventInfo.getStartTime())) {
            throw new BusinessException("活动尚未开始，暂不可签到");
        }
        EventCheckin exists = eventCheckinMapper.selectOne(Wrappers.<EventCheckin>lambdaQuery()
                .eq(EventCheckin::getSignupId, signupId)
                .last("limit 1"));
        if (exists != null) {
            throw new BusinessException("该用户已完成签到");
        }
        EventCheckin eventCheckin = new EventCheckin();
        eventCheckin.setEventId(signup.getEventId());
        eventCheckin.setSignupId(signupId);
        eventCheckin.setUserId(signup.getUserId());
        eventCheckin.setCheckinStatus(CheckinStatusEnum.CHECKED_IN.getCode());
        eventCheckin.setCheckinTime(LocalDateTime.now());
        eventCheckinMapper.insert(eventCheckin);
        adjustCheckinCount(signup.getEventId());
    }

    private void adjustCheckinCount(Long eventId) {
        for (int retry = 0; retry < 3; retry++) {
            EventInfo eventInfo = eventInfoMapper.selectById(eventId);
            if (eventInfo == null) {
                throw new BusinessException("活动不存在");
            }
            int newCheckinCount = eventInfo.getCheckinCount() + 1;
            if (newCheckinCount > eventInfo.getApprovedCount()) {
                throw new BusinessException("签到人数不能大于审核通过人数");
            }
            eventInfo.setCheckinCount(newCheckinCount);
            if (eventInfoMapper.updateById(eventInfo) == 1) {
                return;
            }
        }
        throw new BusinessException("签到人数更新冲突，请稍后重试");
    }
}
