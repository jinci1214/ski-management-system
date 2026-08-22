package com.ski.ticketing.pojo.vo;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentVO {

    private Integer id;
    private String name;
    private BigDecimal price;
    private Integer total;
    private Integer remaining;
}