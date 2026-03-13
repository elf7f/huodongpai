package com.huodongpai.common.util;

import com.huodongpai.entity.EventInfo;
import com.huodongpai.exception.BusinessException;

public final class EventCounterHelper {

    private EventCounterHelper() {
    }

    public static void applyDelta(EventInfo eventInfo, int signupDelta, int approvedDelta, int checkinDelta) {
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
    }
}
