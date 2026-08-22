package com.ski.ticketing.pojo.vo;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*订单详情
* */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVO {

    private Integer id;
    private String orderId;
    private String orderType;
    private Integer ticketId;
    private Integer equipmentId;
    private Integer courseId;
    private String productName;
    private BigDecimal amount;
    private String status;
    private String paymentMethod;
    private String cancelReason;

    private String userName;
    private String userPhone;

    private LocalDateTime createTime;
    private LocalDateTime endTime;
}
