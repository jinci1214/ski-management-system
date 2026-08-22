package com.ski.ticketing.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 黑龙江滑雪场基本信息。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkiResort {
    private Integer id;
    private String name;
    private String city;
    private String address;
    private String phone;
    private LocalTime openTime;
    private LocalTime closeTime;
    private String introduction;
    private String image;
    /** 0-停业，1-营业 */
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
