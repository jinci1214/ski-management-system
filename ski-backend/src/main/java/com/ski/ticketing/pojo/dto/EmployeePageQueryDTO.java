package com.ski.ticketing.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePageQueryDTO {
    private int pageNum = 1;
    private int pageSize = 10;
}
