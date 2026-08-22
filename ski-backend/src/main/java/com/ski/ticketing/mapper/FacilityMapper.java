package com.ski.ticketing.mapper;

import com.ski.ticketing.pojo.entity.Facility;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FacilityMapper {
    Facility getById(Long id);
    List<Facility> getAll();
    List<Facility> getByResortId(Long resortId);
    List<Facility> getByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);
    long count();
    int insert(Facility facility);
    int update(Facility facility);
    int delete(Long id);
}
