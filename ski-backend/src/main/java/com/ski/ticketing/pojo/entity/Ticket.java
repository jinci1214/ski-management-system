package com.ski.ticketing.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/*
* 雪票*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    /**
     * 主键 ID
     */
    private Integer id;
    
    /**
     * 票种名称 (初级票/中级票/高级票等)
     */
    private String name;
    
    /**
     * 关联的雪道 ID
     */
    private Integer skiRunId;
    
    /**
     * 票价
     */
    private BigDecimal price;
    
    /**
     * 有效期 (小时)
     */
    private Integer hour;
    
    /**
     * 使用说明
     */
    private String description;
    
    /**
     * 状态 (0-禁用/1-启用)
     */
    private Integer status;
}
