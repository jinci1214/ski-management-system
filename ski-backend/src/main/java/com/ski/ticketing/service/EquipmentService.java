package com.ski.ticketing.service;

import com.ski.ticketing.pojo.entity.Equipment;
import com.ski.ticketing.pojo.vo.PageResultVO;

import java.util.List;

public interface EquipmentService {

    Equipment getById(Long id);

    List<Equipment> getAll();

    PageResultVO<Equipment> getByPage(int pageNum, int pageSize);

    Equipment create(Equipment equipment);

    Equipment update(Long id, Equipment equipment);

    void delete(Long id);

    void decreaseRemaining(Long id);
}