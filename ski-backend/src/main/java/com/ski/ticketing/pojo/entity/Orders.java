package com.ski.ticketing.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/*
* 订单*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orders {
    /**
     * 主键 ID
     */
    private Integer id;
    
    /**
     * 订单号
     */
    private String orderId;
    
    /**
     * 游客 ID
     */
    private Integer userId;
    
    /**
     * 订单类型 (雪票/雪具租赁/课程)
     */
    private String orderType;
    
    /**
     * 雪票 ID (可为 NULL)
     */
    private Integer ticketId;
    
    /**
     * 雪具 ID (可为 NULL)
     */
    private Integer equipmentId;
    
    /**
     * 课程 ID (可为 NULL)
     */
    private Integer courseId;
    
    /**
     * 金额
     */
    private BigDecimal amount;
    
    /**
     * 支付状态 (待支付/已支付/已退款)
     */
    private String status;
    
    /**
     * 支付方式 (微信/支付宝/现金)
     */
    private String paymentMethod;
    
    /**
     * 取消原因
     */
    private String cancelReason;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
}
