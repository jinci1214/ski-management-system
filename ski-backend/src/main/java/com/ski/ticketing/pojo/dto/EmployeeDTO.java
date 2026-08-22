package com.ski.ticketing.pojo.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

    private Integer id;
    private String username;
    private String name;
    private String phone;
    private Integer status;
}