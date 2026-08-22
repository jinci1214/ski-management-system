package com.ski.ticketing.mapper;

import com.ski.ticketing.pojo.entity.Equipment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EquipmentMapper {

    Equipment getById(Long id);

    List<Equipment> getAll();

    List<Equipment> getByPage(int offset, int pageSize);

    long count();

    int insert(Equipment equipment);

    int update(Equipment equipment);

    int delete(Long id);

    int decreaseRemaining(Long id);

    int increaseRemaining(Long id);
}
