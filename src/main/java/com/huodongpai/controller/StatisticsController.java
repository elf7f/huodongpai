package com.huodongpai.controller;

import com.huodongpai.common.annotation.RequireRole;
import com.huodongpai.common.enums.UserRoleEnum;
import com.huodongpai.common.result.ApiResponse;
import com.huodongpai.dto.statistics.HotEventQueryDTO;
import com.huodongpai.dto.statistics.SignupTrendQueryDTO;
import com.huodongpai.service.StatisticsService;
import com.huodongpai.vo.statistics.DashboardVO;
import com.huodongpai.vo.statistics.HotEventVO;
import com.huodongpai.vo.statistics.SignupTrendVO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
@RequireRole(UserRoleEnum.ADMIN)
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/dashboard")
    public ApiResponse<DashboardVO> dashboard() {
        return ApiResponse.success(statisticsService.getDashboard());
    }

    @GetMapping("/hot-events")
    public ApiResponse<List<HotEventVO>> hotEvents(@Valid HotEventQueryDTO queryDTO) {
        return ApiResponse.success(statisticsService.getHotEvents(queryDTO));
    }

    @GetMapping("/signup-trend")
    public ApiResponse<List<SignupTrendVO>> signupTrend(@Valid SignupTrendQueryDTO queryDTO) {
        return ApiResponse.success(statisticsService.getSignupTrend(queryDTO));
    }
}
