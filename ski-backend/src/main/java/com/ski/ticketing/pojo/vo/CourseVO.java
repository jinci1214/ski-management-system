package com.ski.ticketing.pojo.vo;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseVO {

    private Integer id;
    private String title;
    private String teacher;
    private String skiRunName;
    private Integer remainingCount;
    private BigDecimal price;
    private LocalDateTime courseTime;
    private Integer hours;
    private String level;
}