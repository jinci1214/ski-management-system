package com.ski.ticketing.pojo.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDTO {
/*
* 要使注解生效必须在controller里使用 @Validated，在本类的前面写 */
    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    @NotBlank(message = "订单类型不能为空")
    private String orderType; // 雪票 / 雪具租赁 / 课程

    private Integer ticketId;
    private Integer equipmentId;
    private Integer courseId;

    @NotNull(message = "金额不能为空")
    @DecimalMin("0.01")
    private BigDecimal amount;

    private String paymentMethod;
}