package com.ski.ticketing.mapper;

import com.ski.ticketing.pojo.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    Employee getById(Long id);

    Employee getByUsername(String username);

    List<Employee> getByPosition(String position);

    List<Employee> getByPage(int offset, int pageSize);

    long count();

    int insert(Employee employee);

    int update(Employee employee);

    int updateStatus(Long id, Integer status);

    int delete(Long id);
}
