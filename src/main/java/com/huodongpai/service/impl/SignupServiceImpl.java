package com.huodongpai.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huodongpai.common.enums.SignupOperationTypeEnum;
import com.huodongpai.common.enums.SignupStatusEnum;
import com.huodongpai.common.policy.SignupPolicy;
import com.huodongpai.common.result.PageResponse;
import com.huodongpai.common.util.EventCounterHelper;
import com.huodongpai.converter.SignupConverter;
import com.huodongpai.dto.signup.MySignupPageQueryDTO;
import com.huodongpai.dto.signup.SignupAuditDTO;
import com.huodongpai.dto.signup.SignupPageQueryDTO;
import com.huodongpai.entity.EventCheckin;
import com.huodongpai.entity.EventInfo;
import com.huodongpai.entity.EventSignup;
import com.huodongpai.entity.EventSignupLog;
import com.huodongpai.entity.SysUser;
import com.huodongpai.exception.BusinessException;
import com.huodongpai.mapper.EventCheckinMapper;
import com.huodongpai.mapper.EventInfoMapper;
import com.huodongpai.mapper.EventSignupLogMapper;
import com.huodongpai.mapper.EventSignupMapper;
import com.huodongpai.mapper.SysUserMapper;
import com.huodongpai.service.SignupService;
import com.huodongpai.vo.signup.MySignupPageVO;
import com.huodongpai.vo.signup.SignupAdminPageVO;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupServiceImpl implements SignupService {

    private final EventSignupMapper eventSignupMapper;
    private final EventInfoMapper eventInfoMapper;
    private final EventCheckinMapper eventCheckinMapper;
    private final SysUserMapper sysUserMapper;
    private final EventSignupLogMapper eventSignupLogMapper;

    public SignupServiceImpl(EventSignupMapper eventSignupMapper,
                             EventInfoMapper eventInfoMapper,
                             EventCheckinMapper eventCheckinMapper,
                             SysUserMapper sysUserMapper,
                             EventSignupLogMapper eventSignupLogMapper) {
        this.eventSignupMapper = eventSignupMapper;
        this.eventInfoMapper = eventInfoMapper;
        this.eventCheckinMapper = eventCheckinMapper;
        this.sysUserMapper = sysUserMapper;
        this.eventSignupLogMapper = eventSignupLogMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void apply(Long eventId, Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        SignupPolicy.ensureApplicantEnabled(user);
        EventInfo eventInfo = requireEvent(eventId);
        EventSignup signup = eventSignupMapper.selectOne(Wrappers.<EventSignup>lambdaQuery()
                .eq(EventSignup::getEventId, eventId)
                .eq(EventSignup::getUserId, userId)
                .last("limit 1"));
        SignupPolicy.ensureSignupAllowed(eventInfo, signup);
        String newStatus = SignupPolicy.resolveInitialStatus(eventInfo);
        adjustEventCounters(eventInfo.getId(), 1, SignupStatusEnum.APPROVED.getCode().equals(newStatus) ? 1 : 0, 0);
        LocalDateTime now = LocalDateTime.now();
        signup = SignupConverter.prepareApplyRecord(signup, eventId, userId, newStatus, now);
        if (signup.getId() == null) {
            eventSignupMapper.insert(signup);
        } else {
            if (eventSignupMapper.updateById(signup) != 1) {
                throw new BusinessException("报名失败，请稍后重试");
            }
        }
        saveApplyLog(signup, userId);
    }

    @Override
    public PageResponse<MySignupPageVO> getMyPage(MySignupPageQueryDTO queryDTO, Long userId) {
        IPage<MySignupPageVO> page = eventSignupMapper.selectMyPage(new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()), userId, queryDTO);
        return PageResponse.of(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long signupId, Long userId) {
        EventSignup signup = requireSignup(signupId);
        EventInfo eventInfo = requireEvent(signup.getEventId());
        EventCheckin checkin = eventCheckinMapper.selectOne(Wrappers.<EventCheckin>lambdaQuery()
                .eq(EventCheckin::getSignupId, signupId)
                .last("limit 1"));
        SignupPolicy.ensureCancelable(signup, userId, eventInfo, checkin);
        int approvedDelta = SignupPolicy.resolveApprovedDeltaWhenCancel(signup);
        signup.setStatus(SignupStatusEnum.CANCELLED.getCode());
        signup.setCancelTime(LocalDateTime.now());
        if (eventSignupMapper.updateById(signup) != 1) {
            throw new BusinessException("取消报名失败，请稍后重试");
        }
        adjustEventCounters(signup.getEventId(), -1, approvedDelta, 0);
        saveSignupLog(signup, userId, SignupOperationTypeEnum.CANCEL, null);
    }

    @Override
    public PageResponse<SignupAdminPageVO> getAdminPage(SignupPageQueryDTO queryDTO) {
        IPage<SignupAdminPageVO> page = eventSignupMapper.selectAdminPage(new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()), queryDTO);
        return PageResponse.of(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditPass(Long signupId, SignupAuditDTO auditDTO, Long adminId) {
        EventSignup signup = requirePendingSignup(signupId);
        signup.setStatus(SignupStatusEnum.APPROVED.getCode());
        signup.setRemark(auditDTO.getRemark());
        signup.setAuditBy(adminId);
        signup.setAuditTime(LocalDateTime.now());
        if (eventSignupMapper.updateById(signup) != 1) {
            throw new BusinessException("审核失败，请稍后重试");
        }
        adjustEventCounters(signup.getEventId(), 0, 1, 0);
        saveSignupLog(signup, adminId, SignupOperationTypeEnum.AUDIT_PASS, auditDTO.getRemark());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditReject(Long signupId, SignupAuditDTO auditDTO, Long adminId) {
        EventSignup signup = requirePendingSignup(signupId);
        signup.setStatus(SignupStatusEnum.REJECTED.getCode());
        signup.setRemark(auditDTO.getRemark());
        signup.setAuditBy(adminId);
        signup.setAuditTime(LocalDateTime.now());
        if (eventSignupMapper.updateById(signup) != 1) {
            throw new BusinessException("审核失败，请稍后重试");
        }
        adjustEventCounters(signup.getEventId(), -1, 0, 0);
        saveSignupLog(signup, adminId, SignupOperationTypeEnum.AUDIT_REJECT, auditDTO.getRemark());
    }

    private EventInfo requireEvent(Long eventId) {
        EventInfo eventInfo = eventInfoMapper.selectById(eventId);
        if (eventInfo == null) {
            throw new BusinessException("活动不存在");
        }
        return eventInfo;
    }

    private EventSignup requireSignup(Long signupId) {
        EventSignup signup = eventSignupMapper.selectById(signupId);
        if (signup == null) {
            throw new BusinessException("报名记录不存在");
        }
        return signup;
    }

    private EventSignup requirePendingSignup(Long signupId) {
        EventSignup signup = requireSignup(signupId);
        EventInfo eventInfo = requireEvent(signup.getEventId());
        SignupPolicy.ensurePendingForAudit(signup, eventInfo);
        return signup;
    }

    private void adjustEventCounters(Long eventId, int signupDelta, int approvedDelta, int checkinDelta) {
        for (int retry = 0; retry < 3; retry++) {
            EventInfo eventInfo = requireEvent(eventId);
            EventCounterHelper.applyDelta(eventInfo, signupDelta, approvedDelta, checkinDelta);
            if (eventInfoMapper.updateById(eventInfo) == 1) {
                return;
            }
        }
        throw new BusinessException("活动人数更新冲突，请稍后重试");
    }

    private void saveApplyLog(EventSignup signup, Long userId) {
        saveSignupLog(signup, userId, SignupOperationTypeEnum.APPLY, null);
    }

    private void saveSignupLog(EventSignup signup, Long operatorId, SignupOperationTypeEnum operationType, String remark) {
        EventSignupLog signupLog = SignupConverter.toLog(signup, operatorId, operationType, remark, LocalDateTime.now());
        eventSignupLogMapper.insert(signupLog);
    }
}
