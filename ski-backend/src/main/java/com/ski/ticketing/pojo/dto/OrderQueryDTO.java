package com.ski.ticketing.pojo.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderQueryDTO extends PageRequestDTO {

    private Integer userId;
    private String orderType;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}