package com.datagrokr.service;

import com.datagrokr.repository.EmployeeRepository;
import com.datagrokr.repository.EmployeeRepository2;
import com.datagrokr.repository.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
@EnableTransactionManagement
public class EmployeeService {
    @Autowired
    EmployeeRepository employeeRepository;

    @Transactional
    public List<Employee> findAll(){
        return employeeRepository.findAll();
    }

    @Transactional
    public Employee save(Employee entity){
        Employee result = employeeRepository.save(entity);
        return result;
    }
}
