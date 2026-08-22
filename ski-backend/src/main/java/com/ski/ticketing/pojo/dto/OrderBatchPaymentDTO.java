package com.ski.ticketing.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/** 游客对同一笔组合购买中的多个订单统一支付。 */
@Data
public class OrderBatchPaymentDTO {

    @NotEmpty(message = "订单不能为空")
    private List<Long> orderIds;

    @NotBlank(message = "请选择支付方式")
    private String paymentMethod;
}
