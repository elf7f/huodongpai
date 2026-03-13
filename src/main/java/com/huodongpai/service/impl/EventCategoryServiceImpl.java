package com.huodongpai.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.huodongpai.converter.EventCategoryConverter;
import com.huodongpai.dto.category.CategoryListQueryDTO;
import com.huodongpai.dto.category.CategorySaveDTO;
import com.huodongpai.entity.EventCategory;
import com.huodongpai.entity.EventInfo;
import com.huodongpai.exception.BusinessException;
import com.huodongpai.mapper.EventCategoryMapper;
import com.huodongpai.mapper.EventInfoMapper;
import com.huodongpai.service.EventCategoryService;
import com.huodongpai.vo.category.CategoryVO;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class EventCategoryServiceImpl implements EventCategoryService {

    private final EventCategoryMapper eventCategoryMapper;
    private final EventInfoMapper eventInfoMapper;

    public EventCategoryServiceImpl(EventCategoryMapper eventCategoryMapper, EventInfoMapper eventInfoMapper) {
        this.eventCategoryMapper = eventCategoryMapper;
        this.eventInfoMapper = eventInfoMapper;
    }

    @Override
    public List<CategoryVO> list(CategoryListQueryDTO queryDTO, boolean includeDisabled) {
        Integer statusFilter = queryDTO.getStatus();
        if (!includeDisabled) {
            statusFilter = 1;
        }
        return eventCategoryMapper.selectList(Wrappers.<EventCategory>lambdaQuery()
                        .like(StringUtils.hasText(queryDTO.getKeyword()), EventCategory::getCategoryName, queryDTO.getKeyword())
                        .eq(statusFilter != null, EventCategory::getStatus, statusFilter)
                        .orderByAsc(EventCategory::getSortNum)
                        .orderByDesc(EventCategory::getId))
                .stream()
                .map(EventCategoryConverter::toVO)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(CategorySaveDTO saveDTO) {
        checkDuplicateName(saveDTO.getCategoryName(), null);
        EventCategory category = EventCategoryConverter.toEntity(saveDTO);
        eventCategoryMapper.insert(category);
        return category.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CategorySaveDTO saveDTO) {
        if (saveDTO.getId() == null) {
            throw new BusinessException("分类ID不能为空");
        }
        EventCategory category = requireCategory(saveDTO.getId());
        checkDuplicateName(saveDTO.getCategoryName(), category.getId());
        EventCategoryConverter.applySaveDTO(category, saveDTO);
        if (eventCategoryMapper.updateById(category) != 1) {
            throw new BusinessException("分类更新失败，请稍后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        requireCategory(id);
        long usedCount = eventInfoMapper.selectCount(Wrappers.<EventInfo>lambdaQuery().eq(EventInfo::getCategoryId, id));
        if (usedCount > 0) {
            throw new BusinessException("该分类已被活动使用，不能删除");
        }
        if (eventCategoryMapper.deleteById(id) != 1) {
            throw new BusinessException("分类删除失败，请稍后重试");
        }
    }

    private void checkDuplicateName(String categoryName, Long excludeId) {
        long count = eventCategoryMapper.selectCount(Wrappers.<EventCategory>lambdaQuery()
                .eq(EventCategory::getCategoryName, categoryName.trim())
                .ne(excludeId != null, EventCategory::getId, excludeId));
        if (count > 0) {
            throw new BusinessException("分类名称已存在");
        }
    }

    private EventCategory requireCategory(Long id) {
        EventCategory category = eventCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        return category;
    }
}
