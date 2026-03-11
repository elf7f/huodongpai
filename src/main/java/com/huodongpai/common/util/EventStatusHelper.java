package com.huodongpai.common.util;

import com.huodongpai.common.enums.EventBaseStatusEnum;
import com.huodongpai.common.enums.EventRuntimeStatusEnum;
import com.huodongpai.entity.EventInfo;
import java.time.LocalDateTime;

public final class EventStatusHelper {

    private EventStatusHelper() {
    }

    public static String resolveRuntimeStatus(EventInfo eventInfo) {
        LocalDateTime now = LocalDateTime.now();
        if (EventBaseStatusEnum.DRAFT.getCode().equals(eventInfo.getStatus())) {
            return EventRuntimeStatusEnum.DRAFT.getCode();
        }
        if (EventBaseStatusEnum.CANCELLED.getCode().equals(eventInfo.getStatus())) {
            return EventRuntimeStatusEnum.CANCELLED.getCode();
        }
        if (now.isBefore(eventInfo.getSignupDeadline())) {
            return EventRuntimeStatusEnum.SIGNUP_OPEN.getCode();
        }
        if (!now.isBefore(eventInfo.getSignupDeadline()) && now.isBefore(eventInfo.getStartTime())) {
            return EventRuntimeStatusEnum.SIGNUP_CLOSED.getCode();
        }
        if (!now.isBefore(eventInfo.getStartTime()) && now.isBefore(eventInfo.getEndTime())) {
            return EventRuntimeStatusEnum.ONGOING.getCode();
        }
        return EventRuntimeStatusEnum.FINISHED.getCode();
    }
}
