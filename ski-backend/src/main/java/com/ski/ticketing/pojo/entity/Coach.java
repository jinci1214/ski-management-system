package com.ski.ticketing.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 滑雪教练。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coach {
    private Integer id;
    private String name;
    private String gender;
    private String phone;
    private String level;
    private Integer yearsOfExperience;
    private String certificate;
    /** 0-停用，1-可预约 */
    private Integer status;
    private String introduction;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
