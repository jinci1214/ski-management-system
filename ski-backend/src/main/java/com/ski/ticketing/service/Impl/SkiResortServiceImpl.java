package com.ski.ticketing.service.Impl;

import com.ski.ticketing.mapper.SkiResortMapper;
import com.ski.ticketing.pojo.entity.SkiResort;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.service.SkiResortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkiResortServiceImpl implements SkiResortService {
    @Autowired
    private SkiResortMapper mapper;

    public SkiResort getById(Long id) { return mapper.getById(id); }
    public List<SkiResort> getAll() { return mapper.getAll(); }
    public PageResultVO<SkiResort> getByPage(int pageNum, int pageSize) {
        PageResultVO<SkiResort> result = new PageResultVO<>();
        result.setData(mapper.getByPage((pageNum - 1) * pageSize, pageSize));
        result.setTotal(mapper.count());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }
    public SkiResort create(SkiResort resort) {
        if (resort.getStatus() == null) resort.setStatus(1);
        mapper.insert(resort);
        return resort;
    }
    public SkiResort update(Long id, SkiResort resort) {
        resort.setId(id.intValue());
        mapper.update(resort);
        return mapper.getById(id);
    }
    public void delete(Long id) { mapper.delete(id); }
}
