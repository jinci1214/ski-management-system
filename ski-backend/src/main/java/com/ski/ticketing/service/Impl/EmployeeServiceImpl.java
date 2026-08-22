package com.ski.ticketing.service.Impl;

import com.ski.ticketing.pojo.dto.EmployeeLoginDTO;
import com.ski.ticketing.pojo.dto.EmployeePageQueryDTO;
import com.ski.ticketing.pojo.entity.Employee;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.service.EmployeeService;
import com.ski.ticketing.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public Employee getById(Long id) {
        return employeeMapper.getById(id);
    }

    @Override
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        Employee employee = employeeMapper.getByUsername(employeeLoginDTO.getUsername());

        if (employee == null) {
            throw new RuntimeException("用户名不存在");
        }

        if (!employee.getPassword().equals(employeeLoginDTO.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        if (employee.getStatus() == 0) {
            throw new RuntimeException("账号已禁用");
        }

        employee.setPassword(null);
        return employee;
    }

    @Override
    public Employee create(Employee employee) {
        employeeMapper.insert(employee);
        return employee;
    }

    @Override
    public void delete(Long id) {
        employeeMapper.delete(id);
    }

    @Override
    public Employee update(Long id, Employee employee) {
        employee.setId(id.intValue());
        employeeMapper.update(employee);
        return employeeMapper.getById(id);
    }

    @Override
    public List<Employee> getByPosition(String position) {
        return employeeMapper.getByPosition(position);
    }

    @Override
    public PageResultVO<Employee> getByPage(EmployeePageQueryDTO pageQueryDTO) {
        int offset = (pageQueryDTO.getPageNum() - 1) * pageQueryDTO.getPageSize();
        List<Employee> list = employeeMapper.getByPage(offset, pageQueryDTO.getPageSize());
        long total = employeeMapper.count();

        PageResultVO<Employee> result = new PageResultVO<>();
        result.setData(list);
        result.setTotal(total);
        result.setPageNum(pageQueryDTO.getPageNum());
        result.setPageSize(pageQueryDTO.getPageSize());

        return result;
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        employeeMapper.updateStatus(id, status);
    }
}
