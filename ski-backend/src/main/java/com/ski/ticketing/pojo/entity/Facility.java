package com.ski.ticketing.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 缆车、造雪机、服务大厅等基础设施。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Facility {
    private Integer id;
    private Integer resortId;
    private String name;
    private String type;
    private String location;
    /** 正常、维护中、停用 */
    private String status;
    private LocalDateTime lastCheckTime;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
