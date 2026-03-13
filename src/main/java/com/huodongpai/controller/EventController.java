package com.huodongpai.controller;

import com.huodongpai.common.annotation.RequireRole;
import com.huodongpai.common.context.UserContextHolder;
import com.huodongpai.common.enums.UserRoleEnum;
import com.huodongpai.common.model.LoginUser;
import com.huodongpai.common.result.ApiResponse;
import com.huodongpai.common.result.PageResponse;
import com.huodongpai.dto.event.EventPageQueryDTO;
import com.huodongpai.dto.event.EventSaveDTO;
import com.huodongpai.service.EventService;
import com.huodongpai.service.TokenService;
import com.huodongpai.vo.event.EventDetailVO;
import com.huodongpai.vo.event.EventPageVO;
import jakarta.validation.Valid;
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
@RequestMapping("/api/event")
/**
 * 活动接口控制器。
 * 同时对外提供用户端活动查询接口和管理员活动管理接口。
 */
public class EventController {

    private final EventService eventService;
    private final TokenService tokenService;

    public EventController(EventService eventService, TokenService tokenService) {
        this.eventService = eventService;
        this.tokenService = tokenService;
    }

    /**
     * 用户端活动分页列表。
     * 只返回公开可见的活动数据。
     */
    @GetMapping("/page")
    public ApiResponse<PageResponse<EventPageVO>> page(@Valid EventPageQueryDTO queryDTO) {
        return ApiResponse.success(eventService.getPublicPage(queryDTO));
    }

    /**
     * 管理端活动分页列表。
     * 管理员可查看所有基础状态活动，并按运行状态筛选。
     */
    @RequireRole(UserRoleEnum.ADMIN)
    @GetMapping("/manage/page")
    public ApiResponse<PageResponse<EventPageVO>> managePage(@Valid EventPageQueryDTO queryDTO) {
        return ApiResponse.success(eventService.getManagePage(queryDTO));
    }

    /**
     * 活动详情。
     * 如果请求中携带登录 Token，会额外补充“当前用户对该活动的报名状态”。
     */
    @GetMapping("/{id}")
    public ApiResponse<EventDetailVO> detail(@PathVariable("id") Long id,
                                             @RequestHeader(value = "Authorization", required = false) String authorization) {
        LoginUser loginUser = tokenService.tryParseLoginUser(authorization);
        boolean isAdmin = loginUser != null && UserRoleEnum.ADMIN.getCode().equals(loginUser.getRole());
        Long currentUserId = loginUser == null ? null : loginUser.getId();
        return ApiResponse.success(eventService.getDetail(id, currentUserId, isAdmin));
    }

    /**
     * 新增活动。
     * 创建后默认保存为草稿状态，不会直接发布。
     */
    @RequireRole(UserRoleEnum.ADMIN)
    @PostMapping("/add")
    public ApiResponse<Long> add(@Valid @RequestBody EventSaveDTO saveDTO) {
        return ApiResponse.success(eventService.create(saveDTO, UserContextHolder.getRequired().getId()));
    }

    /**
     * 编辑活动。
     * 仅允许编辑未开始、未取消的活动。
     */
    @RequireRole(UserRoleEnum.ADMIN)
    @PutMapping("/update")
    public ApiResponse<Void> update(@Valid @RequestBody EventSaveDTO saveDTO) {
        eventService.update(saveDTO);
        return ApiResponse.success();
    }

    /**
     * 删除活动。
     * 仅当活动还没有任何报名记录时才允许删除。
     */
    @RequireRole(UserRoleEnum.ADMIN)
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        eventService.delete(id);
        return ApiResponse.success();
    }

    /**
     * 发布活动。
     * 发布前会做完整的业务校验，例如时间范围和人数限制是否合法。
     */
    @RequireRole(UserRoleEnum.ADMIN)
    @PutMapping("/publish/{id}")
    public ApiResponse<Void> publish(@PathVariable("id") Long id) {
        eventService.publish(id);
        return ApiResponse.success();
    }

    /**
     * 取消活动。
     * 取消是业务状态变更，不是物理删除。
     */
    @RequireRole(UserRoleEnum.ADMIN)
    @PutMapping("/cancel/{id}")
    public ApiResponse<Void> cancel(@PathVariable("id") Long id) {
        eventService.cancel(id);
        return ApiResponse.success();
    }
}
