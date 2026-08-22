package com.ski.ticketing.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
/*
* 雪具*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Equipment {
    /**
     * 主键 ID
     */
    private Integer id;

    /**
     * 所属滑雪场 ID
     */
    private Integer resortId;
    
    /**
     * 雪具类型 (单板、双板等)
     */
    private String name;
    
    /**
     * 每日租金
     */
    private BigDecimal price;
    
    /**
     * 总数量
     */
    private Integer total;
    
    /**
     * 剩余可租数量
     */
    private Integer remaining;
    
    /**
     * 超时租金费率
     */
    private BigDecimal overtimeFee;
    
    /**
     * 押金金额
     */
    private BigDecimal deposit;
}
