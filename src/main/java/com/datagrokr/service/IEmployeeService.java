package com.datagrokr.service;

import com.datagrokr.model.Employee;

import java.util.List;
import java.util.Optional;

public interface IEmployeeService {
    public List<Employee> findAll();
    public Optional<Employee> findById(int id);
    public Employee save(Employee entity);
    public Employee update(Employee entity);
}
