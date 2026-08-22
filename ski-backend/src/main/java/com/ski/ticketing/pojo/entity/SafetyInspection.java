package com.ski.ticketing.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 雪道安全巡检记录。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SafetyInspection {
    private Integer id;
    private Integer skiRunId;
    private Integer employeeId;
    private LocalDateTime inspectionTime;
    private String snowCondition;
    /** 安全、需处理、已关闭 */
    private String safetyStatus;
    private String problem;
    private String action;
    private LocalDateTime createTime;
}
