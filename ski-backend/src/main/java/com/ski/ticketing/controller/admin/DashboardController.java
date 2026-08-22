package com.ski.ticketing.controller.admin;

import com.ski.ticketing.pojo.vo.DashboardStatisticsVO;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/statistics")
    public Result<DashboardStatisticsVO> getStatistics() {
        return Result.success(dashboardService.getStatistics());
    }
}
