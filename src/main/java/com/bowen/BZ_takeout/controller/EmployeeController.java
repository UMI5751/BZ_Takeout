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
//@RestController=@controller+@responsebody，会将返回的内容，设置为json格式
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    //HttpServletRequest request: 这是一个来自Java Servlet API的对象，它代表HTTP请求。在Spring控制器方法中，它可以用来获取关于当前请求的详细信息，例如查询参数、头信息等。
    //@RequestBody Employee employee: 这里，@RequestBody 是一个Spring注解，表示请求的主体部分应该被转换（或反序列化）为 Employee 类型的对象。通常，这意味着客户端发送了一个JSON或XML格式的请求体，并且Spring将尝试将其转换为一个 Employee 对象。
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //在使用 Mybatis-plus 时，通常，当你使用 Employee::getUserage 这样的方法引用，Mybatis-plus 会基于命名约定解析它为对应的数据库字段。如果你的方法名是 getUserage，则默认情况下，它可能会将其解析为 userage 这样的数据库字段。但这种行为也取决于你的配置和可能存在的其他映射策略。
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

        //在session中设置employee
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("Logout Successfully");
    }

    @PostMapping
    //在Spring框架的@PostMapping注解中，如果你不提供具体的地址（也称为路径或URL模式），那么这个方法的映射地址将默认为它所在的控制器（Controller）类上的@RequestMapping注解中定义的地址。
    public R<String> save(@RequestBody Employee employee, HttpServletRequest request) {
        log.info("add new employee");

        employee.setPassword(DigestUtils.md5DigestAsHex("12345".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        Long empId = (Long) request.getSession().getAttribute("employee");
//
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

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

//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
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
