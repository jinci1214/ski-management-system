package com.ski.ticketing.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
* 雪道*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkiRun {
    /**
     * 主键 ID
     */
    private Integer id;

    /**
     * 所属滑雪场 ID
     */
    private Integer resortId;
    
    /**
     * 雪道名称
     */
    private String name;
    
    /**
     * 雪道等级 (初级/中级/高级/专家级)
     */
    private String level;
    
    /**
     * 雪道长度 (米)
     */
    private BigDecimal length;
    
    /**
     * 落差高度 (米)
     */
    private BigDecimal height;
    
    /**
     * 雪道照片
     */
    private String image;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
