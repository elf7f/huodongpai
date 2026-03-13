package com.huodongpai.converter;

import com.huodongpai.dto.category.CategorySaveDTO;
import com.huodongpai.entity.EventCategory;
import com.huodongpai.vo.category.CategoryVO;

public final class EventCategoryConverter {

    private EventCategoryConverter() {
    }

    public static EventCategory toEntity(CategorySaveDTO saveDTO) {
        EventCategory category = new EventCategory();
        applySaveDTO(category, saveDTO);
        return category;
    }

    public static void applySaveDTO(EventCategory category, CategorySaveDTO saveDTO) {
        category.setCategoryName(saveDTO.getCategoryName().trim());
        category.setSortNum(saveDTO.getSortNum());
        category.setStatus(saveDTO.getStatus());
    }

    public static CategoryVO toVO(EventCategory category) {
        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setId(category.getId());
        categoryVO.setCategoryName(category.getCategoryName());
        categoryVO.setSortNum(category.getSortNum());
        categoryVO.setStatus(category.getStatus());
        categoryVO.setCreateTime(category.getCreateTime());
        categoryVO.setUpdateTime(category.getUpdateTime());
        return categoryVO;
    }
}
