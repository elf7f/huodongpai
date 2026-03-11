package com.huodongpai.service;

import com.huodongpai.common.result.PageResponse;
import com.huodongpai.dto.signup.MySignupPageQueryDTO;
import com.huodongpai.dto.signup.SignupAuditDTO;
import com.huodongpai.dto.signup.SignupPageQueryDTO;
import com.huodongpai.vo.signup.MySignupPageVO;
import com.huodongpai.vo.signup.SignupAdminPageVO;

public interface SignupService {

    void apply(Long eventId, Long userId);

    PageResponse<MySignupPageVO> getMyPage(MySignupPageQueryDTO queryDTO, Long userId);

    void cancel(Long signupId, Long userId);

    PageResponse<SignupAdminPageVO> getAdminPage(SignupPageQueryDTO queryDTO);

    void auditPass(Long signupId, SignupAuditDTO auditDTO, Long adminId);

    void auditReject(Long signupId, SignupAuditDTO auditDTO, Long adminId);
}
