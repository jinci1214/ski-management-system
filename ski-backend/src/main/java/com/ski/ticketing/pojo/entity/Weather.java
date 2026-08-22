package com.ski.ticketing.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Weather {
    /**
     * 主键 ID
     */
    private Integer id;

    /**
     * 所属滑雪场 ID
     */
    private Integer resortId;
    
    /**
     * 日期
     */
    private LocalDate date;
    
    /**
     * 最低温度 (摄氏度)
     */
    private BigDecimal temperatureMin;
    
    /**
     * 最高温度 (摄氏度)
     */
    private BigDecimal temperatureMax;
    
    /**
     * 天气状况 (晴/多云/雪/雨)
     */
    private String weather;
    
    /**
     * 风速 (m/s)
     */
    private BigDecimal wind;
    
    /**
     * 雪质 (新雪/压雪/硬雪)
     */
    private String snow;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
