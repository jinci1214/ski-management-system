package com.ski.ticketing.mapper;

import com.ski.ticketing.pojo.entity.Courses;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CourseMapper {

    Courses getById(Long id);

    List<Courses> getBySkiRunId(Long skiRunId);

    List<Courses> getByLevel(String level);

    List<Courses> getAll();

    List<Courses> getByPage(int offset, int pageSize);

    long count();

    int insert(Courses course);

    int update(Courses course);

    int delete(Long id);

    int decreaseNumber(Long id);

    int increaseNumber(Long id);
}
