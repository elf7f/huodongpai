package com.huodongpai.controller;

import com.huodongpai.common.annotation.RequireRole;
import com.huodongpai.common.enums.UserRoleEnum;
import com.huodongpai.common.result.ApiResponse;
import com.huodongpai.common.result.PageResponse;
import com.huodongpai.dto.checkin.CheckinPageQueryDTO;
import com.huodongpai.service.CheckinService;
import com.huodongpai.vo.checkin.CheckinPageVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkin")
@RequireRole(UserRoleEnum.ADMIN)
/**
 * 签到接口控制器。
 * 仅管理员可访问，用于查看签到名单和执行签到。
 */
public class CheckinController {

    private final CheckinService checkinService;

    public CheckinController(CheckinService checkinService) {
        this.checkinService = checkinService;
    }

    /**
     * 签到分页列表。
     * 列表底层基于审核通过名单左连接签到表查询得到。
     */
    @GetMapping("/page")
    public ApiResponse<PageResponse<CheckinPageVO>> page(@Valid CheckinPageQueryDTO queryDTO) {
        return ApiResponse.success(checkinService.getPage(queryDTO));
    }

    /**
     * 对某条报名记录执行签到。
     */
    @PutMapping("/do/{signupId}")
    public ApiResponse<Void> doCheckin(@PathVariable("signupId") Long signupId) {
        checkinService.doCheckin(signupId);
        return ApiResponse.success();
    }
}
