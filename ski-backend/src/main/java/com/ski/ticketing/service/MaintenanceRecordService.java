package com.ski.ticketing.service;

import com.ski.ticketing.pojo.entity.MaintenanceRecord;
import com.ski.ticketing.pojo.vo.PageResultVO;

import java.util.List;

public interface MaintenanceRecordService {
    MaintenanceRecord getById(Long id);
    List<MaintenanceRecord> getAll();
    PageResultVO<MaintenanceRecord> getByPage(int pageNum, int pageSize);
    MaintenanceRecord create(MaintenanceRecord record);
    MaintenanceRecord update(Long id, MaintenanceRecord record);
    void delete(Long id);
}
