package com.ski.ticketing.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResultVO<T> {
    private List<T> data;
    private long total;
    private int pageNum;
    private int pageSize;
}
