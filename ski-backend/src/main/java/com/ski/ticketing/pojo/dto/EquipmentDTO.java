package com.ski.ticketing.pojo.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentDTO {

    private Integer id;
    private String name;
    private BigDecimal price;
    private Integer total;
    private Integer remaining;
    private BigDecimal overtimeFee;
    private BigDecimal deposit;
}