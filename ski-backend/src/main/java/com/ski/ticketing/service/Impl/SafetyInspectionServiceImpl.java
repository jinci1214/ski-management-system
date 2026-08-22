package com.ski.ticketing.service.Impl;

import com.ski.ticketing.mapper.SafetyInspectionMapper;
import com.ski.ticketing.pojo.entity.SafetyInspection;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.service.SafetyInspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SafetyInspectionServiceImpl implements SafetyInspectionService {
    @Autowired
    private SafetyInspectionMapper mapper;

    public SafetyInspection getById(Long id) { return mapper.getById(id); }
    public List<SafetyInspection> getAll() { return mapper.getAll(); }
    public PageResultVO<SafetyInspection> getByPage(int pageNum, int pageSize) {
        PageResultVO<SafetyInspection> result = new PageResultVO<>();
        result.setData(mapper.getByPage((pageNum - 1) * pageSize, pageSize));
        result.setTotal(mapper.count());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }
    public SafetyInspection create(SafetyInspection inspection) { mapper.insert(inspection); return inspection; }
    public SafetyInspection update(Long id, SafetyInspection inspection) {
        inspection.setId(id.intValue());
        mapper.update(inspection);
        return mapper.getById(id);
    }
    public void delete(Long id) { mapper.delete(id); }
}
