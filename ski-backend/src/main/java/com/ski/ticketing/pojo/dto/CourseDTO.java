package com.ski.ticketing.pojo.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {

    private Integer id;
    private String title;
    private String teacher;
    private Integer skiRunId;
    private Integer number;
    private BigDecimal price;
    private LocalDateTime courseTime;
}