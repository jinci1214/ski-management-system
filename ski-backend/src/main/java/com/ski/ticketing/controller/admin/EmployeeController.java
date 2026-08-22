package com.ski.ticketing.controller.admin;

import com.ski.ticketing.pojo.dto.EmployeeLoginDTO;
import com.ski.ticketing.pojo.dto.EmployeePageQueryDTO;
import com.ski.ticketing.pojo.entity.Employee;
import com.ski.ticketing.pojo.vo.EmployeeLoginVO;
import com.ski.ticketing.pojo.vo.PageResultVO;
import com.ski.ticketing.properties.JwtProperties;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.EmployeeService;
import com.ski.ticketing.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO.getUsername());

        Employee employee = employeeService.login(employeeLoginDTO);

        Map<String, Object> claims = new HashMap<>();
        claims.put("empId", employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(Long.valueOf(employee.getId()))
                .userName(employee.getUsername())
                .name(employee.getName())
                .position(employee.getPosition())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    // 退出登录
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success("退出成功");
    }

    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            employee.setPassword(null);
        }
        return Result.success(employee);
    }

    @GetMapping("/position/{position}")
    public Result<List<Employee>> getByPosition(@PathVariable String position) {
        List<Employee> list = employeeService.getByPosition(position);
        list.forEach(emp -> emp.setPassword(null));
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result<PageResultVO<Employee>> getByPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {

        EmployeePageQueryDTO pageQueryDTO = new EmployeePageQueryDTO();
        pageQueryDTO.setPageNum(pageNum);
        pageQueryDTO.setPageSize(pageSize);

        PageResultVO<Employee> result = employeeService.getByPage(pageQueryDTO);
        result.getData().forEach(emp -> emp.setPassword(null));
        return Result.success(result);
    }

    @PostMapping
    public Result<Employee> create(@RequestBody Employee employee) {
        Employee created = employeeService.create(employee);
        created.setPassword(null);
        return Result.success(created);
    }

    @PutMapping("/{id}")
    public Result<Employee> update(@PathVariable Long id, @RequestBody Employee employee) {
        Employee updated = employeeService.update(id, employee);
        updated.setPassword(null);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/status/{status}")
    public Result<Void> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        employeeService.updateStatus(id, status);
        return Result.success();
    }
}
