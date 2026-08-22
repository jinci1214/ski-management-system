package com.ski.ticketing.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 设施维护记录。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRecord {
    private Integer id;
    private Integer facilityId;
    private Integer employeeId;
    private String content;
    private BigDecimal cost;
    private LocalDateTime maintenanceTime;
    private String result;
    private LocalDateTime createTime;
}
