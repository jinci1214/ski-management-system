package com.ski.ticketing.mapper;

import com.ski.ticketing.pojo.entity.MaintenanceRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MaintenanceRecordMapper {
    MaintenanceRecord getById(Long id);
    List<MaintenanceRecord> getAll();
    List<MaintenanceRecord> getByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);
    long count();
    int insert(MaintenanceRecord record);
    int update(MaintenanceRecord record);
    int delete(Long id);
}
