package com.ski.ticketing.service;

import com.ski.ticketing.pojo.vo.DashboardStatisticsVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class DashboardServiceTests {
    @Autowired
    private DashboardService dashboardService;

    @Test
    void dashboardReturnsCompleteChartData() {
        DashboardStatisticsVO statistics = dashboardService.getStatistics();

        assertNotNull(statistics);
        assertNotNull(statistics.getTodayRevenue());
        assertNotNull(statistics.getAverageRating());
        assertEquals(3, statistics.getOrderTypeDistribution().size());
        assertEquals(7, statistics.getRecentSevenDayRevenue().size());
    }
}
