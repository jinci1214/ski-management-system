package com.ski.ticketing.service.Impl;

import com.ski.ticketing.mapper.CoachMapper;
import com.ski.ticketing.pojo.entity.Coach;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.service.CoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoachServiceImpl implements CoachService {
    @Autowired
    private CoachMapper mapper;

    public Coach getById(Long id) { return mapper.getById(id); }
    public List<Coach> getAll() { return mapper.getAll(); }
    public PageResultVO<Coach> getByPage(int pageNum, int pageSize) {
        PageResultVO<Coach> result = new PageResultVO<>();
        result.setData(mapper.getByPage((pageNum - 1) * pageSize, pageSize));
        result.setTotal(mapper.count());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }
    public Coach create(Coach coach) {
        if (coach.getStatus() == null) coach.setStatus(1);
        mapper.insert(coach);
        return coach;
    }
    public Coach update(Long id, Coach coach) {
        coach.setId(id.intValue());
        mapper.update(coach);
        return mapper.getById(id);
    }
    public void delete(Long id) { mapper.delete(id); }
}
