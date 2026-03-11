package com.huodongpai.service;

import com.huodongpai.dto.category.CategoryListQueryDTO;
import com.huodongpai.dto.category.CategorySaveDTO;
import com.huodongpai.vo.category.CategoryVO;
import java.util.List;

public interface EventCategoryService {

    List<CategoryVO> list(CategoryListQueryDTO queryDTO, boolean includeDisabled);

    Long add(CategorySaveDTO saveDTO);

    void update(CategorySaveDTO saveDTO);

    void delete(Long id);
}
