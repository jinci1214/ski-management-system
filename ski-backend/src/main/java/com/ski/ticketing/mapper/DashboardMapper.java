package com.ski.ticketing.mapper;

import com.ski.ticketing.pojo.vo.DailyRevenueVO;
import com.ski.ticketing.pojo.vo.DashboardStatisticsVO;
import com.ski.ticketing.pojo.vo.OrderTypeStatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DashboardMapper {

    DashboardStatisticsVO getOverview();

    List<OrderTypeStatisticsVO> getOrderTypeDistribution();

    List<DailyRevenueVO> getRecentRevenue(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);
}
