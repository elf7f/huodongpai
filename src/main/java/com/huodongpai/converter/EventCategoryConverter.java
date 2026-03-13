package com.huodongpai.converter;

import com.huodongpai.dto.category.CategorySaveDTO;
import com.huodongpai.entity.EventCategory;
import com.huodongpai.vo.category.CategoryVO;

public final class EventCategoryConverter {

    private EventCategoryConverter() {
    }

    /**
     * 分类保存 DTO 转实体。
     */
    public static EventCategory toEntity(CategorySaveDTO saveDTO) {
        EventCategory category = new EventCategory();
        applySaveDTO(category, saveDTO);
        return category;
    }

    /**
     * 把保存 DTO 的字段写回已有分类实体。
     */
    public static void applySaveDTO(EventCategory category, CategorySaveDTO saveDTO) {
        category.setCategoryName(saveDTO.getCategoryName().trim());
        category.setSortNum(saveDTO.getSortNum());
        category.setStatus(saveDTO.getStatus());
    }

    /**
     * 分类实体转前端展示对象。
     */
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
