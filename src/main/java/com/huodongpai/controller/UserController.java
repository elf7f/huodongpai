package com.huodongpai.controller;

import com.huodongpai.common.annotation.RequireRole;
import com.huodongpai.common.context.UserContextHolder;
import com.huodongpai.common.enums.UserRoleEnum;
import com.huodongpai.common.result.ApiResponse;
import com.huodongpai.common.result.PageResponse;
import com.huodongpai.dto.user.UserAddDTO;
import com.huodongpai.dto.user.UserPageQueryDTO;
import com.huodongpai.dto.user.UserStatusUpdateDTO;
import com.huodongpai.dto.user.UserUpdateDTO;
import com.huodongpai.service.UserService;
import com.huodongpai.vo.user.UserPageVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequireRole(UserRoleEnum.ADMIN)
/**
 * 用户管理接口。
 * 仅管理员可访问，用于系统用户分页查询、新增、编辑和启停。
 */
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户分页查询。
     */
    @GetMapping("/page")
    public ApiResponse<PageResponse<UserPageVO>> page(@Valid UserPageQueryDTO queryDTO) {
        return ApiResponse.success(userService.getPage(queryDTO));
    }

    /**
     * 新增用户。
     */
    @PostMapping("/add")
    public ApiResponse<Long> add(@Valid @RequestBody UserAddDTO addDTO) {
        return ApiResponse.success(userService.add(addDTO, UserContextHolder.getRequired().getId()));
    }

    /**
     * 编辑用户。
     */
    @PutMapping("/update")
    public ApiResponse<Void> update(@Valid @RequestBody UserUpdateDTO updateDTO) {
        userService.update(updateDTO, UserContextHolder.getRequired().getId());
        return ApiResponse.success();
    }

    /**
     * 修改用户启用/禁用状态。
     */
    @PutMapping("/status/{id}")
    public ApiResponse<Void> updateStatus(@PathVariable("id") Long id,
                                          @Valid @RequestBody UserStatusUpdateDTO updateDTO) {
        userService.updateStatus(id, updateDTO, UserContextHolder.getRequired().getId());
        return ApiResponse.success();
    }
}
