package com.ski.ticketing.pojo.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDTO {

    private Integer id;
    private LocalDate date;
    private BigDecimal temperatureMin;
    private BigDecimal temperatureMax;
    private String weather;
    private BigDecimal wind;
    private String snow;
}