package com.huodongpai.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huodongpai.common.enums.EnableStatusEnum;
import com.huodongpai.common.enums.EventBaseStatusEnum;
import com.huodongpai.common.result.PageResponse;
import com.huodongpai.dto.event.EventPageQueryDTO;
import com.huodongpai.dto.event.EventSaveDTO;
import com.huodongpai.entity.EventCategory;
import com.huodongpai.entity.EventInfo;
import com.huodongpai.entity.EventSignup;
import com.huodongpai.exception.BusinessException;
import com.huodongpai.mapper.EventCategoryMapper;
import com.huodongpai.mapper.EventInfoMapper;
import com.huodongpai.mapper.EventSignupMapper;
import com.huodongpai.service.EventService;
import com.huodongpai.vo.event.EventDetailVO;
import com.huodongpai.vo.event.EventPageVO;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class EventServiceImpl implements EventService {

    private final EventInfoMapper eventInfoMapper;
    private final EventCategoryMapper eventCategoryMapper;
    private final EventSignupMapper eventSignupMapper;

    public EventServiceImpl(EventInfoMapper eventInfoMapper,
                            EventCategoryMapper eventCategoryMapper,
                            EventSignupMapper eventSignupMapper) {
        this.eventInfoMapper = eventInfoMapper;
        this.eventCategoryMapper = eventCategoryMapper;
        this.eventSignupMapper = eventSignupMapper;
    }

    @Override
    public PageResponse<EventPageVO> getPublicPage(EventPageQueryDTO queryDTO) {
        IPage<EventPageVO> page = eventInfoMapper.selectPublicPage(new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()), queryDTO);
        return PageResponse.of(page);
    }

    @Override
    public PageResponse<EventPageVO> getManagePage(EventPageQueryDTO queryDTO) {
        IPage<EventPageVO> page = eventInfoMapper.selectManagePage(new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()), queryDTO);
        return PageResponse.of(page);
    }

    @Override
    public EventDetailVO getDetail(Long eventId, Long currentUserId, boolean isAdmin) {
        EventDetailVO detailVO = eventInfoMapper.selectDetailById(eventId);
        if (detailVO == null) {
            throw new BusinessException("活动不存在");
        }
        if (!isAdmin && !EventBaseStatusEnum.PUBLISHED.getCode().equals(detailVO.getBaseStatus())) {
            throw new BusinessException("活动不存在或未发布");
        }
        if (currentUserId != null) {
            EventSignup signup = eventSignupMapper.selectOne(Wrappers.<EventSignup>lambdaQuery()
                    .eq(EventSignup::getEventId, eventId)
                    .eq(EventSignup::getUserId, currentUserId)
                    .last("limit 1"));
            if (signup != null) {
                detailVO.setCurrentUserSignupStatus(signup.getStatus());
            }
        }
        return detailVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(EventSaveDTO saveDTO, Long operatorId) {
        validateEventSaveDTO(saveDTO);
        checkCategory(saveDTO.getCategoryId());
        EventInfo eventInfo = new EventInfo();
        fillEventInfo(eventInfo, saveDTO);
        eventInfo.setStatus(EventBaseStatusEnum.DRAFT.getCode());
        eventInfo.setSignupCount(0);
        eventInfo.setApprovedCount(0);
        eventInfo.setCheckinCount(0);
        eventInfo.setCreateBy(operatorId);
        eventInfo.setVersion(0);
        eventInfoMapper.insert(eventInfo);
        return eventInfo.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(EventSaveDTO saveDTO) {
        if (saveDTO.getId() == null) {
            throw new BusinessException("活动ID不能为空");
        }
        validateEventSaveDTO(saveDTO);
        checkCategory(saveDTO.getCategoryId());
        EventInfo eventInfo = requireEvent(saveDTO.getId());
        if (EventBaseStatusEnum.CANCELLED.getCode().equals(eventInfo.getStatus())) {
            throw new BusinessException("已取消的活动不允许编辑");
        }
        if (!LocalDateTime.now().isBefore(eventInfo.getStartTime())) {
            throw new BusinessException("活动开始后不允许编辑");
        }
        fillEventInfo(eventInfo, saveDTO);
        if (eventInfoMapper.updateById(eventInfo) != 1) {
            throw new BusinessException("活动更新失败，请稍后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long eventId) {
        EventInfo eventInfo = requireEvent(eventId);
        long signupCount = eventSignupMapper.selectCount(Wrappers.<EventSignup>lambdaQuery().eq(EventSignup::getEventId, eventId));
        if (signupCount > 0) {
            throw new BusinessException("活动已有报名记录，不允许删除");
        }
        if (eventInfoMapper.deleteById(eventId) != 1) {
            throw new BusinessException("活动删除失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long eventId) {
        EventInfo eventInfo = requireEvent(eventId);
        if (EventBaseStatusEnum.CANCELLED.getCode().equals(eventInfo.getStatus())) {
            throw new BusinessException("已取消的活动不允许发布");
        }
        validateEventEntity(eventInfo);
        eventInfo.setStatus(EventBaseStatusEnum.PUBLISHED.getCode());
        if (eventInfoMapper.updateById(eventInfo) != 1) {
            throw new BusinessException("活动发布失败，请稍后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long eventId) {
        EventInfo eventInfo = requireEvent(eventId);
        eventInfo.setStatus(EventBaseStatusEnum.CANCELLED.getCode());
        if (eventInfoMapper.updateById(eventInfo) != 1) {
            throw new BusinessException("活动取消失败，请稍后重试");
        }
    }

    private EventInfo requireEvent(Long eventId) {
        EventInfo eventInfo = eventInfoMapper.selectById(eventId);
        if (eventInfo == null) {
            throw new BusinessException("活动不存在");
        }
        return eventInfo;
    }

    private void checkCategory(Long categoryId) {
        EventCategory category = eventCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException("活动分类不存在");
        }
        if (!EnableStatusEnum.ENABLED.getCode().equals(category.getStatus())) {
            throw new BusinessException("活动分类已停用");
        }
    }

    private void validateEventSaveDTO(EventSaveDTO saveDTO) {
        if (saveDTO.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("活动开始时间必须晚于当前时间");
        }
        if (!saveDTO.getSignupDeadline().isBefore(saveDTO.getStartTime())) {
            throw new BusinessException("报名截止时间必须早于活动开始时间");
        }
        if (!saveDTO.getEndTime().isAfter(saveDTO.getStartTime())) {
            throw new BusinessException("活动结束时间必须晚于开始时间");
        }
    }

    private void validateEventEntity(EventInfo eventInfo) {
        if (!StringUtils.hasText(eventInfo.getTitle())) {
            throw new BusinessException("活动标题不能为空");
        }
        if (eventInfo.getMaxParticipants() == null || eventInfo.getMaxParticipants() <= 0) {
            throw new BusinessException("最大报名人数必须大于0");
        }
        if (eventInfo.getStartTime() == null || eventInfo.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("活动开始时间必须晚于当前时间");
        }
        if (eventInfo.getEndTime() == null || !eventInfo.getEndTime().isAfter(eventInfo.getStartTime())) {
            throw new BusinessException("活动结束时间必须晚于开始时间");
        }
        if (eventInfo.getSignupDeadline() == null || !eventInfo.getSignupDeadline().isBefore(eventInfo.getStartTime())) {
            throw new BusinessException("报名截止时间必须早于活动开始时间");
        }
    }

    private void fillEventInfo(EventInfo eventInfo, EventSaveDTO saveDTO) {
        eventInfo.setTitle(saveDTO.getTitle());
        eventInfo.setCategoryId(saveDTO.getCategoryId());
        eventInfo.setCoverUrl(saveDTO.getCoverUrl());
        eventInfo.setLocation(saveDTO.getLocation());
        eventInfo.setStartTime(saveDTO.getStartTime());
        eventInfo.setEndTime(saveDTO.getEndTime());
        eventInfo.setSignupDeadline(saveDTO.getSignupDeadline());
        eventInfo.setMaxParticipants(saveDTO.getMaxParticipants());
        eventInfo.setNeedAudit(saveDTO.getNeedAudit());
        eventInfo.setDescription(saveDTO.getDescription());
    }
}
