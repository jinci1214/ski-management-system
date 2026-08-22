package com.ski.ticketing.service;

import com.ski.ticketing.pojo.dto.EmployeeLoginDTO;
import com.ski.ticketing.pojo.dto.EmployeePageQueryDTO;
import com.ski.ticketing.pojo.entity.Employee;
import com.ski.ticketing.pojo.vo.PageResultVO;

import java.util.List;

public interface EmployeeService {

    Employee getById(Long id);

    Employee login(EmployeeLoginDTO employeeLoginDTO);

    Employee create(Employee employee);

    void delete(Long id);

    Employee update(Long id, Employee employee);

    List<Employee> getByPosition(String position);

    PageResultVO<Employee> getByPage(EmployeePageQueryDTO pageQueryDTO);

    void updateStatus(Long id, Integer status);
}
