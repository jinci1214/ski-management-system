package com.ski.ticketing.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/*
* 滑雪课程*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Courses {
    /**
     * 主键 ID
     */
    private Integer id;
    
    /**
     * 课程名称
     */
    private String title;
    
    /**
     * 教学内容描述
     */
    private String content;
    
    /**
     * 教练姓名
     */
    private String teacher;

    /**
     * 教练 ID
     */
    private Integer coachId;
    
    /**
     * 教学场地雪道 ID
     */
    private Integer skiRunId;
    
    /**
     * 剩余可报名人数
     */
    private Integer number;
    
    /**
     * 教学时间
     */
    private LocalDateTime courseTime;
    
    /**
     * 课程时长 (小时)
     */
    private Integer hours;
    
    /**
     * 课程价格
     */
    private BigDecimal price;
    
    /**
     * 难度等级
     */
    private String level;
}
