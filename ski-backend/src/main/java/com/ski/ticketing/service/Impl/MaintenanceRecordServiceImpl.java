package com.ski.ticketing.service.Impl;

import com.ski.ticketing.mapper.MaintenanceRecordMapper;
import com.ski.ticketing.pojo.entity.MaintenanceRecord;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.service.MaintenanceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaintenanceRecordServiceImpl implements MaintenanceRecordService {
    @Autowired
    private MaintenanceRecordMapper mapper;

    public MaintenanceRecord getById(Long id) { return mapper.getById(id); }
    public List<MaintenanceRecord> getAll() { return mapper.getAll(); }
    public PageResultVO<MaintenanceRecord> getByPage(int pageNum, int pageSize) {
        PageResultVO<MaintenanceRecord> result = new PageResultVO<>();
        result.setData(mapper.getByPage((pageNum - 1) * pageSize, pageSize));
        result.setTotal(mapper.count());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }
    public MaintenanceRecord create(MaintenanceRecord record) { mapper.insert(record); return record; }
    public MaintenanceRecord update(Long id, MaintenanceRecord record) {
        record.setId(id.intValue());
        mapper.update(record);
        return mapper.getById(id);
    }
    public void delete(Long id) { mapper.delete(id); }
}
