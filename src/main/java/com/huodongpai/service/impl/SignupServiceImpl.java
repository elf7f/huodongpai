package com.huodongpai.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huodongpai.common.enums.EnableStatusEnum;
import com.huodongpai.common.enums.EventBaseStatusEnum;
import com.huodongpai.common.enums.EventRuntimeStatusEnum;
import com.huodongpai.common.enums.SignupOperationTypeEnum;
import com.huodongpai.common.enums.SignupStatusEnum;
import com.huodongpai.common.result.PageResponse;
import com.huodongpai.common.util.EventStatusHelper;
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
        if (user == null || !EnableStatusEnum.ENABLED.getCode().equals(user.getStatus())) {
            throw new BusinessException("当前用户不可报名");
        }
        EventInfo eventInfo = requireEvent(eventId);
        if (!EventBaseStatusEnum.PUBLISHED.getCode().equals(eventInfo.getStatus())) {
            throw new BusinessException("活动未发布，暂不可报名");
        }
        if (!EventRuntimeStatusEnum.SIGNUP_OPEN.getCode().equals(EventStatusHelper.resolveRuntimeStatus(eventInfo))) {
            throw new BusinessException("当前活动不在报名时间内");
        }
        EventSignup signup = eventSignupMapper.selectOne(Wrappers.<EventSignup>lambdaQuery()
                .eq(EventSignup::getEventId, eventId)
                .eq(EventSignup::getUserId, userId)
                .last("limit 1"));
        if (signup != null && (SignupStatusEnum.PENDING.getCode().equals(signup.getStatus())
                || SignupStatusEnum.APPROVED.getCode().equals(signup.getStatus()))) {
            throw new BusinessException("请勿重复报名");
        }
        String newStatus = eventInfo.getNeedAudit() != null && eventInfo.getNeedAudit() == 1
                ? SignupStatusEnum.PENDING.getCode()
                : SignupStatusEnum.APPROVED.getCode();
        adjustEventCounters(eventInfo.getId(), 1, SignupStatusEnum.APPROVED.getCode().equals(newStatus) ? 1 : 0, 0);
        LocalDateTime now = LocalDateTime.now();
        if (signup == null) {
            signup = new EventSignup();
            signup.setEventId(eventId);
            signup.setUserId(userId);
        }
        signup.setStatus(newStatus);
        signup.setRemark(null);
        signup.setSignupTime(now);
        signup.setAuditTime(null);
        signup.setAuditBy(null);
        signup.setCancelTime(null);
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
        if (!signup.getUserId().equals(userId)) {
            throw new BusinessException("无权取消他人报名");
        }
        if (!SignupStatusEnum.PENDING.getCode().equals(signup.getStatus())
                && !SignupStatusEnum.APPROVED.getCode().equals(signup.getStatus())) {
            throw new BusinessException("当前报名状态不允许取消");
        }
        EventInfo eventInfo = requireEvent(signup.getEventId());
        if (!LocalDateTime.now().isBefore(eventInfo.getStartTime())) {
            throw new BusinessException("活动开始后不允许取消报名");
        }
        EventCheckin checkin = eventCheckinMapper.selectOne(Wrappers.<EventCheckin>lambdaQuery()
                .eq(EventCheckin::getSignupId, signupId)
                .last("limit 1"));
        if (checkin != null) {
            throw new BusinessException("已签到记录不允许取消报名");
        }
        int approvedDelta = SignupStatusEnum.APPROVED.getCode().equals(signup.getStatus()) ? -1 : 0;
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
        if (!SignupStatusEnum.PENDING.getCode().equals(signup.getStatus())) {
            throw new BusinessException("当前报名记录不在待审核状态");
        }
        EventInfo eventInfo = requireEvent(signup.getEventId());
        if (EventBaseStatusEnum.CANCELLED.getCode().equals(eventInfo.getStatus())) {
            throw new BusinessException("活动已取消，无法审核");
        }
        return signup;
    }

    private void adjustEventCounters(Long eventId, int signupDelta, int approvedDelta, int checkinDelta) {
        for (int retry = 0; retry < 3; retry++) {
            EventInfo eventInfo = requireEvent(eventId);
            int newSignupCount = eventInfo.getSignupCount() + signupDelta;
            int newApprovedCount = eventInfo.getApprovedCount() + approvedDelta;
            int newCheckinCount = eventInfo.getCheckinCount() + checkinDelta;
            if (newSignupCount < 0 || newApprovedCount < 0 || newCheckinCount < 0) {
                throw new BusinessException("活动统计数据异常");
            }
            if (newSignupCount > eventInfo.getMaxParticipants()) {
                throw new BusinessException("活动报名名额已满");
            }
            if (newSignupCount < newApprovedCount || newApprovedCount < newCheckinCount) {
                throw new BusinessException("活动统计数据异常");
            }
            eventInfo.setSignupCount(newSignupCount);
            eventInfo.setApprovedCount(newApprovedCount);
            eventInfo.setCheckinCount(newCheckinCount);
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
        EventSignupLog signupLog = new EventSignupLog();
        signupLog.setSignupId(signup.getId());
        signupLog.setEventId(signup.getEventId());
        signupLog.setUserId(signup.getUserId());
        signupLog.setOperationType(operationType.getCode());
        signupLog.setTargetStatus(signup.getStatus());
        signupLog.setOperatorId(operatorId);
        signupLog.setRemark(remark);
        signupLog.setOperationTime(LocalDateTime.now());
        eventSignupLogMapper.insert(signupLog);
    }
}
