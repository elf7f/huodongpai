package com.huodongpai.service;

import com.huodongpai.dto.statistics.HotEventQueryDTO;
import com.huodongpai.dto.statistics.SignupTrendQueryDTO;
import com.huodongpai.vo.statistics.DashboardVO;
import com.huodongpai.vo.statistics.HotEventVO;
import com.huodongpai.vo.statistics.SignupTrendVO;
import java.util.List;

public interface StatisticsService {

    DashboardVO getDashboard();

    List<HotEventVO> getHotEvents(HotEventQueryDTO queryDTO);

    List<SignupTrendVO> getSignupTrend(SignupTrendQueryDTO queryDTO);
}
