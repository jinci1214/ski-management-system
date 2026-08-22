package com.ski.ticketing.pojo.vo;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeVO {

    private Integer id;
    private String username;
    private String name;
    private String phone;
    private Integer status;
    private LocalDateTime createTime;
}