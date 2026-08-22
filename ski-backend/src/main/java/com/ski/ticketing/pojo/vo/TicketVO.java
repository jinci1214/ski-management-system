package com.ski.ticketing.pojo.vo;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketVO {

    private Integer id;
    private String name;
    private String skiRunName;
    private BigDecimal price;
    private Integer hour;
    private String description;
    private Integer status;
}