package com.huodongpai.converter;

import com.huodongpai.common.enums.CheckinStatusEnum;
import com.huodongpai.entity.EventCheckin;
import com.huodongpai.entity.EventSignup;
import java.time.LocalDateTime;

public final class CheckinConverter {

    private CheckinConverter() {
    }

    /**
     * 报名记录转签到实体。
     * 签到表只记录“签到成功事实”，因此在真正签到时才创建实体。
     */
    public static EventCheckin toEntity(EventSignup signup, LocalDateTime checkinTime) {
        EventCheckin eventCheckin = new EventCheckin();
        eventCheckin.setEventId(signup.getEventId());
        eventCheckin.setSignupId(signup.getId());
        eventCheckin.setUserId(signup.getUserId());
        eventCheckin.setCheckinStatus(CheckinStatusEnum.CHECKED_IN.getCode());
        eventCheckin.setCheckinTime(checkinTime);
        return eventCheckin;
    }
}
