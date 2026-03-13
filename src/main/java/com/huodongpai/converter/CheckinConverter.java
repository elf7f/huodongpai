package com.huodongpai.converter;

import com.huodongpai.common.enums.CheckinStatusEnum;
import com.huodongpai.entity.EventCheckin;
import com.huodongpai.entity.EventSignup;
import java.time.LocalDateTime;

public final class CheckinConverter {

    private CheckinConverter() {
    }

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
