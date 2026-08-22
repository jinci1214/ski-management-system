package com.ski.ticketing.mapper;

import com.ski.ticketing.pojo.entity.Coach;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CoachMapper {
    Coach getById(Long id);
    List<Coach> getAll();
    List<Coach> getByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);
    long count();
    int insert(Coach coach);
    int update(Coach coach);
    int delete(Long id);
}
