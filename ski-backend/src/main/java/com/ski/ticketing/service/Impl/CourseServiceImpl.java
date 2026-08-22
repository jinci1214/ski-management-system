package com.ski.ticketing.service.Impl;

import com.ski.ticketing.pojo.entity.Courses;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.service.CourseService;
import com.ski.ticketing.mapper.CourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public Courses getById(Long id) {
        return courseMapper.getById(id);
    }

    @Override
    public List<Courses> getBySkiRunId(Long skiRunId) {
        return courseMapper.getBySkiRunId(skiRunId);
    }

    @Override
    public List<Courses> getByLevel(String level) {
        return courseMapper.getByLevel(level);
    }

    @Override
    public List<Courses> getAll() {
        return courseMapper.getAll();
    }

    @Override
    public PageResultVO<Courses> getByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Courses> list = courseMapper.getByPage(offset, pageSize);
        long total = courseMapper.count();

        PageResultVO<Courses> result = new PageResultVO<>();
        result.setData(list);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);

        return result;
    }

    @Override
    public Courses create(Courses course) {
        courseMapper.insert(course);
        return course;
    }

    @Override
    public Courses update(Long id, Courses course) {
        course.setId(id.intValue());
        courseMapper.update(course);
        return courseMapper.getById(id);
    }

    @Override
    public void delete(Long id) {
        courseMapper.delete(id);
    }

    @Override
    public void decreaseNumber(Long id) {
        courseMapper.decreaseNumber(id);
    }
}