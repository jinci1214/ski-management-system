package com.ski.ticketing.pojo.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketDTO {

    @NotBlank(message = "票种名称不能为空")
    private String name;

    private Integer skiRunId;

    @NotNull
    @DecimalMin("0")
    private BigDecimal price;

    private Integer hour;
    private String description;
    private Integer status;
}