package com.ski.ticketing.service;

import com.ski.ticketing.pojo.entity.SafetyInspection;
import com.ski.ticketing.pojo.vo.PageResultVO;

import java.util.List;

public interface SafetyInspectionService {
    SafetyInspection getById(Long id);
    List<SafetyInspection> getAll();
    PageResultVO<SafetyInspection> getByPage(int pageNum, int pageSize);
    SafetyInspection create(SafetyInspection inspection);
    SafetyInspection update(Long id, SafetyInspection inspection);
    void delete(Long id);
}
