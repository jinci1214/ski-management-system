package com.ski.ticketing.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 管理端首页统计数据。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatisticsVO {

    private long userCount;
    private long orderCount;
    private long todayOrderCount;
    private BigDecimal todayRevenue;
    private long pendingOrderCount;
    private long resortCount;
    private long normalFacilityCount;
    private BigDecimal averageRating;
    private List<OrderTypeStatisticsVO> orderTypeDistribution;
    private List<DailyRevenueVO> recentSevenDayRevenue;
}
