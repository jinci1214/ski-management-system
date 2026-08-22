package com.ski.ticketing.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 后台首页的每日营业额统计。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyRevenueVO {

    private LocalDate date;
    private BigDecimal revenue;
}
