package com.ski.ticketing.service;

import com.ski.ticketing.pojo.entity.Courses;
import com.ski.ticketing.pojo.vo.PageResultVO;

import java.util.List;

public interface CourseService {

    Courses getById(Long id);

    List<Courses> getBySkiRunId(Long skiRunId);

    List<Courses> getByLevel(String level);

    List<Courses> getAll();

    PageResultVO<Courses> getByPage(int pageNum, int pageSize);

    Courses create(Courses course);

    Courses update(Long id, Courses course);

    void delete(Long id);

    void decreaseNumber(Long id);
}