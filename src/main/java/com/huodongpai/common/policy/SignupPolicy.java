package com.huodongpai.common.policy;

import com.huodongpai.common.enums.EnableStatusEnum;
import com.huodongpai.common.enums.EventRuntimeStatusEnum;
import com.huodongpai.common.enums.SignupStatusEnum;
import com.huodongpai.entity.EventCheckin;
import com.huodongpai.entity.EventInfo;
import com.huodongpai.entity.EventSignup;
import com.huodongpai.entity.SysUser;
import com.huodongpai.exception.BusinessException;
import java.time.LocalDateTime;

public final class SignupPolicy {

    private SignupPolicy() {
    }

    public static void ensureApplicantEnabled(SysUser user) {
        if (user == null || !EnableStatusEnum.ENABLED.getCode().equals(user.getStatus())) {
            throw new BusinessException("当前用户不可报名");
        }
    }

    public static void ensureSignupAllowed(EventInfo eventInfo, EventSignup signup) {
        EventPolicy.ensurePublished(eventInfo, "活动未发布，暂不可报名");
        EventPolicy.ensureRuntimeStatus(eventInfo, EventRuntimeStatusEnum.SIGNUP_OPEN, "当前活动不在报名时间内");
        ensureNoActiveSignup(signup);
    }

    public static void ensureNoActiveSignup(EventSignup signup) {
        if (signup != null && (SignupStatusEnum.PENDING.getCode().equals(signup.getStatus())
                || SignupStatusEnum.APPROVED.getCode().equals(signup.getStatus()))) {
            throw new BusinessException("请勿重复报名");
        }
    }

    public static String resolveInitialStatus(EventInfo eventInfo) {
        return eventInfo.getNeedAudit() != null && eventInfo.getNeedAudit() == 1
                ? SignupStatusEnum.PENDING.getCode()
                : SignupStatusEnum.APPROVED.getCode();
    }

    public static void ensureCancelable(EventSignup signup,
                                        Long currentUserId,
                                        EventInfo eventInfo,
                                        EventCheckin checkin) {
        if (!signup.getUserId().equals(currentUserId)) {
            throw new BusinessException("无权取消他人报名");
        }
        if (!SignupStatusEnum.PENDING.getCode().equals(signup.getStatus())
                && !SignupStatusEnum.APPROVED.getCode().equals(signup.getStatus())) {
            throw new BusinessException("当前报名状态不允许取消");
        }
        if (!LocalDateTime.now().isBefore(eventInfo.getStartTime())) {
            throw new BusinessException("活动开始后不允许取消报名");
        }
        if (checkin != null) {
            throw new BusinessException("已签到记录不允许取消报名");
        }
    }

    public static int resolveApprovedDeltaWhenCancel(EventSignup signup) {
        return SignupStatusEnum.APPROVED.getCode().equals(signup.getStatus()) ? -1 : 0;
    }

    public static void ensurePendingForAudit(EventSignup signup, EventInfo eventInfo) {
        if (!SignupStatusEnum.PENDING.getCode().equals(signup.getStatus())) {
            throw new BusinessException("当前报名记录不在待审核状态");
        }
        EventPolicy.ensureNotCancelled(eventInfo, "活动已取消，无法审核");
    }
}
