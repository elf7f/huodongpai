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
public class EventController {

    private final EventService eventService;
    private final TokenService tokenService;

    public EventController(EventService eventService, TokenService tokenService) {
        this.eventService = eventService;
        this.tokenService = tokenService;
    }

    @GetMapping("/page")
    public ApiResponse<PageResponse<EventPageVO>> page(@Valid EventPageQueryDTO queryDTO) {
        return ApiResponse.success(eventService.getPublicPage(queryDTO));
    }

    @RequireRole(UserRoleEnum.ADMIN)
    @GetMapping("/manage/page")
    public ApiResponse<PageResponse<EventPageVO>> managePage(@Valid EventPageQueryDTO queryDTO) {
        return ApiResponse.success(eventService.getManagePage(queryDTO));
    }

    @GetMapping("/{id}")
    public ApiResponse<EventDetailVO> detail(@PathVariable("id") Long id,
                                             @RequestHeader(value = "Authorization", required = false) String authorization) {
        LoginUser loginUser = tokenService.tryParseLoginUser(authorization);
        boolean isAdmin = loginUser != null && UserRoleEnum.ADMIN.getCode().equals(loginUser.getRole());
        Long currentUserId = loginUser == null ? null : loginUser.getId();
        return ApiResponse.success(eventService.getDetail(id, currentUserId, isAdmin));
    }

    @RequireRole(UserRoleEnum.ADMIN)
    @PostMapping("/add")
    public ApiResponse<Long> add(@Valid @RequestBody EventSaveDTO saveDTO) {
        return ApiResponse.success(eventService.create(saveDTO, UserContextHolder.getRequired().getId()));
    }

    @RequireRole(UserRoleEnum.ADMIN)
    @PutMapping("/update")
    public ApiResponse<Void> update(@Valid @RequestBody EventSaveDTO saveDTO) {
        eventService.update(saveDTO);
        return ApiResponse.success();
    }

    @RequireRole(UserRoleEnum.ADMIN)
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        eventService.delete(id);
        return ApiResponse.success();
    }

    @RequireRole(UserRoleEnum.ADMIN)
    @PutMapping("/publish/{id}")
    public ApiResponse<Void> publish(@PathVariable("id") Long id) {
        eventService.publish(id);
        return ApiResponse.success();
    }

    @RequireRole(UserRoleEnum.ADMIN)
    @PutMapping("/cancel/{id}")
    public ApiResponse<Void> cancel(@PathVariable("id") Long id) {
        eventService.cancel(id);
        return ApiResponse.success();
    }
}
