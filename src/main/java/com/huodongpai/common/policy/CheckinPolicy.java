package com.huodongpai.common.policy;

import com.huodongpai.common.enums.SignupStatusEnum;
import com.huodongpai.entity.EventCheckin;
import com.huodongpai.entity.EventInfo;
import com.huodongpai.entity.EventSignup;
import com.huodongpai.exception.BusinessException;
import java.time.LocalDateTime;

public final class CheckinPolicy {

    private CheckinPolicy() {
    }

    /**
     * 校验签到前置条件。
     * 必须是审核通过的报名，活动不能取消，活动必须已开始，且同一报名记录不能重复签到。
     */
    public static void ensureCheckinAllowed(EventSignup signup, EventInfo eventInfo, EventCheckin existingCheckin) {
        if (!SignupStatusEnum.APPROVED.getCode().equals(signup.getStatus())) {
            throw new BusinessException("仅审核通过的报名记录可签到");
        }
        EventPolicy.ensureNotCancelled(eventInfo, "活动已取消，无法签到");
        if (LocalDateTime.now().isBefore(eventInfo.getStartTime())) {
            throw new BusinessException("活动尚未开始，暂不可签到");
        }
        if (existingCheckin != null) {
            throw new BusinessException("该用户已完成签到");
        }
    }
}
