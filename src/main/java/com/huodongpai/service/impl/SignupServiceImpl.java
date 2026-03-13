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
/**
 * 报名服务实现。
 * 负责报名、取消报名、后台审核以及活动人数统计同步更新。
 */
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

    /**
     * 用户报名。
     * 流程包括用户状态校验、活动可报名校验、重复报名校验、人数占用和报名日志记录。
     */
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

    /**
     * 用户查看自己的报名分页。
     */
    @Override
    public PageResponse<MySignupPageVO> getMyPage(MySignupPageQueryDTO queryDTO, Long userId) {
        IPage<MySignupPageVO> page = eventSignupMapper.selectMyPage(new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()), userId, queryDTO);
        return PageResponse.of(page);
    }

    /**
     * 用户取消报名。
     * 取消成功后需要同步回收活动占用人数。
     */
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

    /**
     * 管理员查看报名记录分页。
     */
    @Override
    public PageResponse<SignupAdminPageVO> getAdminPage(SignupPageQueryDTO queryDTO) {
        IPage<SignupAdminPageVO> page = eventSignupMapper.selectAdminPage(new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()), queryDTO);
        return PageResponse.of(page);
    }

    /**
     * 审核通过报名。
     * 待审核记录通过后会增加 approved_count，但不会再次占用 signup_count。
     */
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

    /**
     * 审核驳回报名。
     * 驳回后释放之前占用的名额。
     */
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

    /**
     * 读取活动，不存在直接抛异常。
     */
    private EventInfo requireEvent(Long eventId) {
        EventInfo eventInfo = eventInfoMapper.selectById(eventId);
        if (eventInfo == null) {
            throw new BusinessException("活动不存在");
        }
        return eventInfo;
    }

    /**
     * 读取报名记录，不存在直接抛异常。
     */
    private EventSignup requireSignup(Long signupId) {
        EventSignup signup = eventSignupMapper.selectById(signupId);
        if (signup == null) {
            throw new BusinessException("报名记录不存在");
        }
        return signup;
    }

    /**
     * 获取待审核报名记录，并校验其所在活动是否还能审核。
     */
    private EventSignup requirePendingSignup(Long signupId) {
        EventSignup signup = requireSignup(signupId);
        EventInfo eventInfo = requireEvent(signup.getEventId());
        SignupPolicy.ensurePendingForAudit(signup, eventInfo);
        return signup;
    }

    /**
     * 统一维护活动统计字段。
     * 这里通过“查询最新活动数据 -> 应用增量 -> 乐观更新”的方式控制并发冲突。
     */
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

    /**
     * 报名提交成功后记录 apply 日志，供趋势统计使用。
     */
    private void saveApplyLog(EventSignup signup, Long userId) {
        saveSignupLog(signup, userId, SignupOperationTypeEnum.APPLY, null);
    }

    /**
     * 写入报名操作日志。
     * 主报名表负责保存“当前状态”，日志表负责保留“历史过程”。
     */
    private void saveSignupLog(EventSignup signup, Long operatorId, SignupOperationTypeEnum operationType, String remark) {
        EventSignupLog signupLog = SignupConverter.toLog(signup, operatorId, operationType, remark, LocalDateTime.now());
        eventSignupLogMapper.insert(signupLog);
    }
}
