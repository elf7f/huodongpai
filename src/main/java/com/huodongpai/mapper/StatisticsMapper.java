package com.huodongpai.mapper;

import com.huodongpai.vo.statistics.DashboardVO;
import com.huodongpai.vo.statistics.HotEventVO;
import com.huodongpai.vo.statistics.SignupTrendVO;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface StatisticsMapper {

    DashboardVO selectDashboard();

    List<HotEventVO> selectHotEvents(@Param("limit") Integer limit);

    List<SignupTrendVO> selectSignupTrend(@Param("startTime") LocalDateTime startTime);
}
