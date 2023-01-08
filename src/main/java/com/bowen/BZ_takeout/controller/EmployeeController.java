package com.bowen.BZ_takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bowen.BZ_takeout.common.R;
import com.bowen.BZ_takeout.entity.Employee;
import com.bowen.BZ_takeout.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.authenticator.DigestAuthenticator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        if (emp == null) {
            return R.error("Login Failed");
        }

        if (!emp.getPassword().equals(password)) {
            return R.error("Login Failed");
        }

        // if employee status is locked
        if (emp.getStatus() == 0) {
            return R.error("Login Failed");
        }

        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("Logout Successfully");
    }

    @PostMapping
    public R<String> save(@RequestBody Employee employee, HttpServletRequest request) {
        log.info("add new employee");

        employee.setPassword(DigestUtils.md5DigestAsHex("12345".getBytes()));

        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        Long empId = (Long) request.getSession().getAttribute("employee");

        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);

        employeeService.save(employee);

        return R.success("add employee successfully");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page pageInfo = new Page(page, pageSize);

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);

        queryWrapper.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);

    }

    @PutMapping
    public R<String> update (HttpServletRequest request, @RequestBody Employee employee) {
        log.info(employee.toString());

        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);
        employeeService.updateById(employee);

        return R.success("successfully edited employee information.");
    }


    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("Searching employee by ID");
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }


}
