package com.huodongpai.common.policy;

import com.huodongpai.common.enums.EventBaseStatusEnum;
import com.huodongpai.common.enums.EventRuntimeStatusEnum;
import com.huodongpai.common.util.EventStatusHelper;
import com.huodongpai.dto.event.EventSaveDTO;
import com.huodongpai.entity.EventInfo;
import com.huodongpai.exception.BusinessException;
import java.time.LocalDateTime;
import org.springframework.util.StringUtils;

public final class EventPolicy {

    private EventPolicy() {
    }

    /**
     * 校验“保存活动”请求是否合法。
     * 主要校验标题、开始时间、结束时间、报名截止时间和人数上限。
     */
    public static void validateSaveRequest(EventSaveDTO saveDTO) {
        validateEventTimeRange(saveDTO.getTitle(), saveDTO.getStartTime(), saveDTO.getEndTime(), saveDTO.getSignupDeadline(),
                saveDTO.getMaxParticipants());
    }

    /**
     * 校验活动是否允许编辑。
     * 已取消活动和已开始活动都不允许编辑。
     */
    public static void ensureEditable(EventInfo eventInfo) {
        ensureNotCancelled(eventInfo, "已取消的活动不允许编辑");
        if (!LocalDateTime.now().isBefore(eventInfo.getStartTime())) {
            throw new BusinessException("活动开始后不允许编辑");
        }
    }

    /**
     * 校验活动是否允许发布。
     * 发布前既要确认不是取消状态，也要重新检查核心业务字段是否完整合法。
     */
    public static void ensurePublishable(EventInfo eventInfo) {
        ensureNotCancelled(eventInfo, "已取消的活动不允许发布");
        validateEventTimeRange(eventInfo.getTitle(), eventInfo.getStartTime(), eventInfo.getEndTime(), eventInfo.getSignupDeadline(),
                eventInfo.getMaxParticipants());
    }

    /**
     * 校验活动是否已发布。
     */
    public static void ensurePublished(EventInfo eventInfo, String message) {
        if (!EventBaseStatusEnum.PUBLISHED.getCode().equals(eventInfo.getStatus())) {
            throw new BusinessException(message);
        }
    }

    /**
     * 校验活动当前运行状态是否符合预期。
     * 运行状态不直接落库，而是根据时间实时推导。
     */
    public static void ensureRuntimeStatus(EventInfo eventInfo, EventRuntimeStatusEnum expectedStatus, String message) {
        String actualStatus = EventStatusHelper.resolveRuntimeStatus(eventInfo);
        if (!expectedStatus.getCode().equals(actualStatus)) {
            throw new BusinessException(message);
        }
    }

    /**
     * 校验活动不是已取消状态。
     */
    public static void ensureNotCancelled(EventInfo eventInfo, String message) {
        if (EventBaseStatusEnum.CANCELLED.getCode().equals(eventInfo.getStatus())) {
            throw new BusinessException(message);
        }
    }

    /**
     * 活动核心时间/人数规则校验。
     */
    private static void validateEventTimeRange(String title,
                                               LocalDateTime startTime,
                                               LocalDateTime endTime,
                                               LocalDateTime signupDeadline,
                                               Integer maxParticipants) {
        if (!StringUtils.hasText(title)) {
            throw new BusinessException("活动标题不能为空");
        }
        if (maxParticipants == null || maxParticipants <= 0) {
            throw new BusinessException("最大报名人数必须大于0");
        }
        if (startTime == null || startTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException("活动开始时间必须晚于当前时间");
        }
        if (endTime == null || !endTime.isAfter(startTime)) {
            throw new BusinessException("活动结束时间必须晚于开始时间");
        }
        if (signupDeadline == null || !signupDeadline.isBefore(startTime)) {
            throw new BusinessException("报名截止时间必须早于活动开始时间");
        }
    }
}
