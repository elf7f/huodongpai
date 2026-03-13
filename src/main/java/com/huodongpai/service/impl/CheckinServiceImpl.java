package com.huodongpai.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huodongpai.common.policy.CheckinPolicy;
import com.huodongpai.common.result.PageResponse;
import com.huodongpai.common.util.EventCounterHelper;
import com.huodongpai.converter.CheckinConverter;
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
        EventInfo eventInfo = eventInfoMapper.selectById(signup.getEventId());
        if (eventInfo == null) {
            throw new BusinessException("活动不存在");
        }
        EventCheckin exists = eventCheckinMapper.selectOne(Wrappers.<EventCheckin>lambdaQuery()
                .eq(EventCheckin::getSignupId, signupId)
                .last("limit 1"));
        CheckinPolicy.ensureCheckinAllowed(signup, eventInfo, exists);
        EventCheckin eventCheckin = CheckinConverter.toEntity(signup, java.time.LocalDateTime.now());
        eventCheckinMapper.insert(eventCheckin);
        adjustCheckinCount(signup.getEventId());
    }

    private void adjustCheckinCount(Long eventId) {
        for (int retry = 0; retry < 3; retry++) {
            EventInfo eventInfo = eventInfoMapper.selectById(eventId);
            if (eventInfo == null) {
                throw new BusinessException("活动不存在");
            }
            EventCounterHelper.applyDelta(eventInfo, 0, 0, 1);
            if (eventInfoMapper.updateById(eventInfo) == 1) {
                return;
            }
        }
        throw new BusinessException("签到人数更新冲突，请稍后重试");
    }
}
