package com.ski.ticketing.mapper;

import com.ski.ticketing.pojo.entity.SkiRun;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SkiRunMapper {

    SkiRun getById(Long id);

    List<SkiRun> getAll();

    List<SkiRun> getByLevel(String level);

    List<SkiRun> getByPage(int offset, int pageSize);

    long count();

    int insert(SkiRun skiRun);

    int update(SkiRun skiRun);

    int delete(Long id);

    int deleteTicketsBySkiRunId(Long skiRunId);

    int deleteCoursesBySkiRunId(Long skiRunId);
}