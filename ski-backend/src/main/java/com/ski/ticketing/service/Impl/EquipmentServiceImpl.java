package com.ski.ticketing.service.Impl;

import com.ski.ticketing.pojo.entity.Equipment;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.service.EquipmentService;
import com.ski.ticketing.mapper.EquipmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Override
    public Equipment getById(Long id) {
        return equipmentMapper.getById(id);
    }

    @Override
    public List<Equipment> getAll() {
        return equipmentMapper.getAll();
    }

    @Override
    public PageResultVO<Equipment> getByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Equipment> list = equipmentMapper.getByPage(offset, pageSize);
        long total = equipmentMapper.count();

        PageResultVO<Equipment> result = new PageResultVO<>();
        result.setData(list);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);

        return result;
    }

    @Override
    public Equipment create(Equipment equipment) {
        equipmentMapper.insert(equipment);
        return equipment;
    }

    @Override
    public Equipment update(Long id, Equipment equipment) {
        equipment.setId(id.intValue());
        equipmentMapper.update(equipment);
        return equipmentMapper.getById(id);
    }

    @Override
    public void delete(Long id) {
        equipmentMapper.delete(id);
    }

    @Override
    public void decreaseRemaining(Long id) {
        equipmentMapper.decreaseRemaining(id);
    }
}