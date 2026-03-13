package com.huodongpai.converter;

import com.huodongpai.common.enums.SignupOperationTypeEnum;
import com.huodongpai.entity.EventSignup;
import com.huodongpai.entity.EventSignupLog;
import java.time.LocalDateTime;

public final class SignupConverter {

    private SignupConverter() {
    }

    /**
     * 准备报名记录。
     * 如果是首次报名则创建新实体；如果是重新报名则复用旧实体并覆盖当前状态。
     */
    public static EventSignup prepareApplyRecord(EventSignup signup,
                                                 Long eventId,
                                                 Long userId,
                                                 String status,
                                                 LocalDateTime signupTime) {
        EventSignup target = signup == null ? new EventSignup() : signup;
        if (target.getId() == null) {
            target.setEventId(eventId);
            target.setUserId(userId);
        }
        target.setStatus(status);
        target.setRemark(null);
        target.setSignupTime(signupTime);
        target.setAuditTime(null);
        target.setAuditBy(null);
        target.setCancelTime(null);
        return target;
    }

    /**
     * 报名记录转日志实体。
     * 日志表负责记录历史行为，供统计和问题追踪使用。
     */
    public static EventSignupLog toLog(EventSignup signup,
                                       Long operatorId,
                                       SignupOperationTypeEnum operationType,
                                       String remark,
                                       LocalDateTime operationTime) {
        EventSignupLog signupLog = new EventSignupLog();
        signupLog.setSignupId(signup.getId());
        signupLog.setEventId(signup.getEventId());
        signupLog.setUserId(signup.getUserId());
        signupLog.setOperationType(operationType.getCode());
        signupLog.setTargetStatus(signup.getStatus());
        signupLog.setOperatorId(operatorId);
        signupLog.setRemark(remark);
        signupLog.setOperationTime(operationTime);
        return signupLog;
    }
}
