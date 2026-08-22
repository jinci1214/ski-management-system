package com.ski.ticketing.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data

@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDTO {

    @NotNull
    private Integer pageNum = 1;

    @NotNull
    private Integer pageSize = 10;
}