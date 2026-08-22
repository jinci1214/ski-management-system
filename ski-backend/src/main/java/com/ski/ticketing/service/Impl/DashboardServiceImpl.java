package com.ski.ticketing.service.Impl;

import com.ski.ticketing.contant.OrderType;
import com.ski.ticketing.mapper.DashboardMapper;
import com.ski.ticketing.pojo.vo.DailyRevenueVO;
import com.ski.ticketing.pojo.vo.DashboardStatisticsVO;
import com.ski.ticketing.pojo.vo.OrderTypeStatisticsVO;
import com.ski.ticketing.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardMapper dashboardMapper;

    @Override
    public DashboardStatisticsVO getStatistics() {
        DashboardStatisticsVO statistics = dashboardMapper.getOverview();
        statistics.setOrderTypeDistribution(buildOrderTypeDistribution());
        statistics.setRecentSevenDayRevenue(buildRecentSevenDayRevenue());
        return statistics;
    }

    /**
     * 即使某一类暂时没有订单，也返回 0，方便前端直接绘制饼图。
     */
    private List<OrderTypeStatisticsVO> buildOrderTypeDistribution() {
        Map<String, Long> countByType = dashboardMapper.getOrderTypeDistribution().stream()
                .collect(Collectors.toMap(OrderTypeStatisticsVO::getOrderType,
                        OrderTypeStatisticsVO::getCount));

        List<OrderTypeStatisticsVO> result = new ArrayList<>();
        result.add(new OrderTypeStatisticsVO(OrderType.TICKET,
                countByType.getOrDefault(OrderType.TICKET, 0L)));
        result.add(new OrderTypeStatisticsVO(OrderType.EQUIPMENT,
                countByType.getOrDefault(OrderType.EQUIPMENT, 0L)));
        result.add(new OrderTypeStatisticsVO(OrderType.COURSE,
                countByType.getOrDefault(OrderType.COURSE, 0L)));
        return result;
    }

    /**
     * 数据库只返回有营业额的日期，服务层补齐最近 7 天的零值。
     */
    private List<DailyRevenueVO> buildRecentSevenDayRevenue() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        Map<LocalDate, DailyRevenueVO> revenueByDate = dashboardMapper
                .getRecentRevenue(startDate, endDate)
                .stream()
                .collect(Collectors.toMap(DailyRevenueVO::getDate, Function.identity()));

        Map<LocalDate, BigDecimal> sevenDays = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            DailyRevenueVO dailyRevenue = revenueByDate.get(date);
            sevenDays.put(date, dailyRevenue == null ? BigDecimal.ZERO : dailyRevenue.getRevenue());
        }

        return sevenDays.entrySet().stream()
                .map(entry -> new DailyRevenueVO(entry.getKey(), entry.getValue()))
                .toList();
    }
}
