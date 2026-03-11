package com.huodongpai.service.impl;

import com.huodongpai.dto.statistics.HotEventQueryDTO;
import com.huodongpai.dto.statistics.SignupTrendQueryDTO;
import com.huodongpai.mapper.StatisticsMapper;
import com.huodongpai.service.StatisticsService;
import com.huodongpai.vo.statistics.DashboardVO;
import com.huodongpai.vo.statistics.HotEventVO;
import com.huodongpai.vo.statistics.SignupTrendVO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final StatisticsMapper statisticsMapper;

    public StatisticsServiceImpl(StatisticsMapper statisticsMapper) {
        this.statisticsMapper = statisticsMapper;
    }

    @Override
    public DashboardVO getDashboard() {
        DashboardVO dashboardVO = statisticsMapper.selectDashboard();
        if (dashboardVO.getTotalSignupCount() == null) {
            dashboardVO.setTotalSignupCount(0);
        }
        if (dashboardVO.getTotalApprovedCount() == null) {
            dashboardVO.setTotalApprovedCount(0);
        }
        if (dashboardVO.getTotalCheckinCount() == null) {
            dashboardVO.setTotalCheckinCount(0);
        }
        dashboardVO.setCheckinRate(calculateRate(dashboardVO.getTotalCheckinCount(), dashboardVO.getTotalApprovedCount()));
        dashboardVO.setHotEvents(fillRate(statisticsMapper.selectHotEvents(5)));
        return dashboardVO;
    }

    @Override
    public List<HotEventVO> getHotEvents(HotEventQueryDTO queryDTO) {
        return fillRate(statisticsMapper.selectHotEvents(queryDTO.getLimit()));
    }

    @Override
    public List<SignupTrendVO> getSignupTrend(SignupTrendQueryDTO queryDTO) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(queryDTO.getDays() - 1L);
        List<SignupTrendVO> rows = statisticsMapper.selectSignupTrend(startDate.atStartOfDay());
        Map<String, Long> countMap = new LinkedHashMap<>();
        for (SignupTrendVO row : rows) {
            countMap.put(row.getDate(), row.getSignupCount());
        }
        List<SignupTrendVO> result = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(today); date = date.plusDays(1)) {
            SignupTrendVO trendVO = new SignupTrendVO();
            trendVO.setDate(date.format(DATE_FORMATTER));
            trendVO.setSignupCount(countMap.getOrDefault(trendVO.getDate(), 0L));
            result.add(trendVO);
        }
        return result;
    }

    private List<HotEventVO> fillRate(List<HotEventVO> hotEvents) {
        for (HotEventVO hotEvent : hotEvents) {
            hotEvent.setCheckinRate(calculateRate(hotEvent.getCheckinCount(), hotEvent.getApprovedCount()));
        }
        return hotEvents;
    }

    private BigDecimal calculateRate(Integer numerator, Integer denominator) {
        if (denominator == null || denominator == 0 || numerator == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(numerator.longValue())
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(denominator.longValue()), 2, RoundingMode.HALF_UP);
    }
}
