package com.ski.ticketing.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 后台首页的订单类型统计。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderTypeStatisticsVO {

    private String orderType;
    private long count;
}
