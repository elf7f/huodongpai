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
public class CheckinController {

    private final CheckinService checkinService;

    public CheckinController(CheckinService checkinService) {
        this.checkinService = checkinService;
    }

    @GetMapping("/page")
    public ApiResponse<PageResponse<CheckinPageVO>> page(@Valid CheckinPageQueryDTO queryDTO) {
        return ApiResponse.success(checkinService.getPage(queryDTO));
    }

    @PutMapping("/do/{signupId}")
    public ApiResponse<Void> doCheckin(@PathVariable("signupId") Long signupId) {
        checkinService.doCheckin(signupId);
        return ApiResponse.success();
    }
}
