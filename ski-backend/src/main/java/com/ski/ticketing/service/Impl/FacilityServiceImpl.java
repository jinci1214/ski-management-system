package com.ski.ticketing.service.Impl;

import com.ski.ticketing.mapper.FacilityMapper;
import com.ski.ticketing.pojo.entity.Facility;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacilityServiceImpl implements FacilityService {
    @Autowired
    private FacilityMapper mapper;

    public Facility getById(Long id) { return mapper.getById(id); }
    public List<Facility> getAll() { return mapper.getAll(); }
    public List<Facility> getByResortId(Long resortId) { return mapper.getByResortId(resortId); }
    public PageResultVO<Facility> getByPage(int pageNum, int pageSize) {
        PageResultVO<Facility> result = new PageResultVO<>();
        result.setData(mapper.getByPage((pageNum - 1) * pageSize, pageSize));
        result.setTotal(mapper.count());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }
    public Facility create(Facility facility) {
        if (facility.getStatus() == null) facility.setStatus("正常");
        mapper.insert(facility);
        return facility;
    }
    public Facility update(Long id, Facility facility) {
        facility.setId(id.intValue());
        mapper.update(facility);
        return mapper.getById(id);
    }
    public void delete(Long id) { mapper.delete(id); }
}
