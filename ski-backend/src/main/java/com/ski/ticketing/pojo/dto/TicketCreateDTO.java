package com.ski.ticketing.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketCreateDTO {
    private String name;
    private Integer skiRunId;
    private BigDecimal price;
    private Integer hour;
    private String description;
    private Integer status;
}