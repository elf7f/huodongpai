package com.huodongpai.controller;

import com.huodongpai.common.annotation.RequireRole;
import com.huodongpai.common.enums.UserRoleEnum;
import com.huodongpai.common.model.LoginUser;
import com.huodongpai.common.result.ApiResponse;
import com.huodongpai.dto.category.CategoryListQueryDTO;
import com.huodongpai.dto.category.CategorySaveDTO;
import com.huodongpai.exception.BusinessException;
import com.huodongpai.service.EventCategoryService;
import com.huodongpai.service.TokenService;
import com.huodongpai.vo.category.CategoryVO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/event-category")
public class EventCategoryController {

    private final EventCategoryService eventCategoryService;
    private final TokenService tokenService;

    public EventCategoryController(EventCategoryService eventCategoryService, TokenService tokenService) {
        this.eventCategoryService = eventCategoryService;
        this.tokenService = tokenService;
    }

    @GetMapping("/list")
    public ApiResponse<List<CategoryVO>> list(@Valid CategoryListQueryDTO queryDTO,
                                              @RequestHeader(value = "Authorization", required = false) String authorization) {
        boolean includeDisabled = Boolean.TRUE.equals(queryDTO.getIncludeDisabled());
        if (includeDisabled) {
            LoginUser loginUser = tokenService.tryParseLoginUser(authorization);
            if (loginUser == null || !UserRoleEnum.ADMIN.getCode().equals(loginUser.getRole())) {
                throw new BusinessException("仅管理员可查看全部分类");
            }
        }
        return ApiResponse.success(eventCategoryService.list(queryDTO, includeDisabled));
    }

    @RequireRole(UserRoleEnum.ADMIN)
    @PostMapping("/add")
    public ApiResponse<Long> add(@Valid @RequestBody CategorySaveDTO saveDTO) {
        return ApiResponse.success(eventCategoryService.add(saveDTO));
    }

    @RequireRole(UserRoleEnum.ADMIN)
    @PutMapping("/update")
    public ApiResponse<Void> update(@Valid @RequestBody CategorySaveDTO saveDTO) {
        eventCategoryService.update(saveDTO);
        return ApiResponse.success();
    }

    @RequireRole(UserRoleEnum.ADMIN)
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        eventCategoryService.delete(id);
        return ApiResponse.success();
    }
}
