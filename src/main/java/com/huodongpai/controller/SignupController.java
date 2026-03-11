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
public class SignupController {

    private final SignupService signupService;

    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    @RequireLogin
    @PostMapping("/apply/{eventId}")
    public ApiResponse<Void> apply(@PathVariable("eventId") Long eventId) {
        signupService.apply(eventId, UserContextHolder.getRequired().getId());
        return ApiResponse.success();
    }

    @RequireLogin
    @GetMapping("/my-page")
    public ApiResponse<PageResponse<MySignupPageVO>> myPage(@Valid MySignupPageQueryDTO queryDTO) {
        return ApiResponse.success(signupService.getMyPage(queryDTO, UserContextHolder.getRequired().getId()));
    }

    @RequireLogin
    @PutMapping("/cancel/{signupId}")
    public ApiResponse<Void> cancel(@PathVariable("signupId") Long signupId) {
        signupService.cancel(signupId, UserContextHolder.getRequired().getId());
        return ApiResponse.success();
    }

    @RequireRole(UserRoleEnum.ADMIN)
    @GetMapping("/page")
    public ApiResponse<PageResponse<SignupAdminPageVO>> page(@Valid SignupPageQueryDTO queryDTO) {
        return ApiResponse.success(signupService.getAdminPage(queryDTO));
    }

    @RequireRole(UserRoleEnum.ADMIN)
    @PutMapping("/audit/pass/{signupId}")
    public ApiResponse<Void> auditPass(@PathVariable("signupId") Long signupId,
                                       @Valid @RequestBody(required = false) SignupAuditDTO auditDTO) {
        signupService.auditPass(signupId, auditDTO == null ? new SignupAuditDTO() : auditDTO, UserContextHolder.getRequired().getId());
        return ApiResponse.success();
    }

    @RequireRole(UserRoleEnum.ADMIN)
    @PutMapping("/audit/reject/{signupId}")
    public ApiResponse<Void> auditReject(@PathVariable("signupId") Long signupId,
                                         @Valid @RequestBody(required = false) SignupAuditDTO auditDTO) {
        signupService.auditReject(signupId, auditDTO == null ? new SignupAuditDTO() : auditDTO, UserContextHolder.getRequired().getId());
        return ApiResponse.success();
    }
}
