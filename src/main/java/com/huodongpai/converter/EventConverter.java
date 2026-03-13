package com.huodongpai.converter;

import com.huodongpai.common.enums.EventBaseStatusEnum;
import com.huodongpai.dto.event.EventSaveDTO;
import com.huodongpai.entity.EventInfo;
import org.springframework.util.StringUtils;

public final class EventConverter {

    private EventConverter() {
    }

    public static EventInfo toNewEntity(EventSaveDTO saveDTO, Long operatorId) {
        EventInfo eventInfo = new EventInfo();
        applySaveDTO(eventInfo, saveDTO);
        eventInfo.setStatus(EventBaseStatusEnum.DRAFT.getCode());
        eventInfo.setSignupCount(0);
        eventInfo.setApprovedCount(0);
        eventInfo.setCheckinCount(0);
        eventInfo.setCreateBy(operatorId);
        eventInfo.setVersion(0);
        return eventInfo;
    }

    public static void applySaveDTO(EventInfo eventInfo, EventSaveDTO saveDTO) {
        eventInfo.setTitle(StringUtils.trimWhitespace(saveDTO.getTitle()));
        eventInfo.setCategoryId(saveDTO.getCategoryId());
        eventInfo.setCoverUrl(StringUtils.trimWhitespace(saveDTO.getCoverUrl()));
        eventInfo.setLocation(StringUtils.trimWhitespace(saveDTO.getLocation()));
        eventInfo.setStartTime(saveDTO.getStartTime());
        eventInfo.setEndTime(saveDTO.getEndTime());
        eventInfo.setSignupDeadline(saveDTO.getSignupDeadline());
        eventInfo.setMaxParticipants(saveDTO.getMaxParticipants());
        eventInfo.setNeedAudit(saveDTO.getNeedAudit());
        eventInfo.setDescription(saveDTO.getDescription());
    }
}
