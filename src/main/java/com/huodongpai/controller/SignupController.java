package com.huodongpai.controller;

import com.huodongpai.common.annotation.RequireLogin;
import com.huodongpai.common.annotation.RequireRole;
import com.huodongpai.common.context.UserContextHolder;
import com.huodongpai.common.enums.UserRoleEnum;
import com.huodongpai.common.result.ApiResponse;
import com.huodongpai.common.result.PageResponse;
import com.huodongpai.dto.signup.MySignupPageQueryDTO;
import com.huodongpai.dto.signup.SignupAuditDTO;
import com.huodongpai.dto.signup.SignupPageQueryDTO;
import com.huodongpai.service.SignupService;
import com.huodongpai.vo.signup.MySignupPageVO;
import com.huodongpai.vo.signup.SignupAdminPageVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/signup")
/**
 * 报名相关接口。
 * 包含用户报名、我的报名列表、取消报名，以及管理员审核报名记录等功能。
 */
public class SignupController {

    private final SignupService signupService;

    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    /**
     * 用户提交报名。
     */
    @RequireLogin
    @PostMapping("/apply/{eventId}")
    public ApiResponse<Void> apply(@PathVariable("eventId") Long eventId) {
        signupService.apply(eventId, UserContextHolder.getRequired().getId());
        return ApiResponse.success();
    }

    /**
     * 用户查看自己的报名分页列表。
     */
    @RequireLogin
    @GetMapping("/my-page")
    public ApiResponse<PageResponse<MySignupPageVO>> myPage(@Valid MySignupPageQueryDTO queryDTO) {
        return ApiResponse.success(signupService.getMyPage(queryDTO, UserContextHolder.getRequired().getId()));
    }

    /**
     * 用户取消自己的报名。
     */
    @RequireLogin
    @PutMapping("/cancel/{signupId}")
    public ApiResponse<Void> cancel(@PathVariable("signupId") Long signupId) {
        signupService.cancel(signupId, UserContextHolder.getRequired().getId());
        return ApiResponse.success();
    }

    /**
     * 管理员查看报名记录列表。
     */
    @RequireRole(UserRoleEnum.ADMIN)
    @GetMapping("/page")
    public ApiResponse<PageResponse<SignupAdminPageVO>> page(@Valid SignupPageQueryDTO queryDTO) {
        return ApiResponse.success(signupService.getAdminPage(queryDTO));
    }

    /**
     * 管理员审核通过报名。
     * 如果前端没有传审核备注，会自动补一个空 DTO，避免空指针。
     */
    @RequireRole(UserRoleEnum.ADMIN)
    @PutMapping("/audit/pass/{signupId}")
    public ApiResponse<Void> auditPass(@PathVariable("signupId") Long signupId,
                                       @Valid @RequestBody(required = false) SignupAuditDTO auditDTO) {
        signupService.auditPass(signupId, auditDTO == null ? new SignupAuditDTO() : auditDTO, UserContextHolder.getRequired().getId());
        return ApiResponse.success();
    }

    /**
     * 管理员审核驳回报名。
     */
    @RequireRole(UserRoleEnum.ADMIN)
    @PutMapping("/audit/reject/{signupId}")
    public ApiResponse<Void> auditReject(@PathVariable("signupId") Long signupId,
                                         @Valid @RequestBody(required = false) SignupAuditDTO auditDTO) {
        signupService.auditReject(signupId, auditDTO == null ? new SignupAuditDTO() : auditDTO, UserContextHolder.getRequired().getId());
        return ApiResponse.success();
    }
}
