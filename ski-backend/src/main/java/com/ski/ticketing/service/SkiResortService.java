package com.ski.ticketing.service;

import com.ski.ticketing.pojo.entity.SkiResort;
import com.ski.ticketing.pojo.vo.PageResultVO;

import java.util.List;

public interface SkiResortService {
    SkiResort getById(Long id);
    List<SkiResort> getAll();
    PageResultVO<SkiResort> getByPage(int pageNum, int pageSize);
    SkiResort create(SkiResort resort);
    SkiResort update(Long id, SkiResort resort);
    void delete(Long id);
}
