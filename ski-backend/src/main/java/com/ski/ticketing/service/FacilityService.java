package com.ski.ticketing.service;

import com.ski.ticketing.pojo.entity.Facility;
import com.ski.ticketing.pojo.vo.PageResultVO;

import java.util.List;

public interface FacilityService {
    Facility getById(Long id);
    List<Facility> getAll();
    List<Facility> getByResortId(Long resortId);
    PageResultVO<Facility> getByPage(int pageNum, int pageSize);
    Facility create(Facility facility);
    Facility update(Long id, Facility facility);
    void delete(Long id);
}
