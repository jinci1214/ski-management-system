package com.ski.ticketing.mapper;

import com.ski.ticketing.pojo.entity.SafetyInspection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SafetyInspectionMapper {
    SafetyInspection getById(Long id);
    List<SafetyInspection> getAll();
    List<SafetyInspection> getByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);
    long count();
    int insert(SafetyInspection inspection);
    int update(SafetyInspection inspection);
    int delete(Long id);
}
