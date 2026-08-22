package com.ski.ticketing.mapper;

import com.ski.ticketing.pojo.entity.SkiResort;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SkiResortMapper {
    SkiResort getById(Long id);
    List<SkiResort> getAll();
    List<SkiResort> getByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);
    long count();
    int insert(SkiResort resort);
    int update(SkiResort resort);
    int delete(Long id);
}
