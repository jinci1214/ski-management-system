package com.ski.ticketing.service;

import com.ski.ticketing.pojo.entity.Coach;
import com.ski.ticketing.pojo.vo.PageResultVO;

import java.util.List;

public interface CoachService {
    Coach getById(Long id);
    List<Coach> getAll();
    PageResultVO<Coach> getByPage(int pageNum, int pageSize);
    Coach create(Coach coach);
    Coach update(Long id, Coach coach);
    void delete(Long id);
}
