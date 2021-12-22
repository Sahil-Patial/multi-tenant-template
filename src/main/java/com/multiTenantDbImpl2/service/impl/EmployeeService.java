package com.multiTenantDbImpl2.service.impl;

import com.multiTenantDbImpl2.repository.model.Employee;
import com.multiTenantDbImpl2.repository.EmployeeRepository;
import com.multiTenantDbImpl2.service.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService implements IEmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee addEmp(Employee input) {
        employeeRepository.save(input);
        return input;
    }

    @Override
    public void updateEmp(Employee input) {
        employeeRepository.save(input);
    }

    @Override
    public List<Employee> listEmp() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee getEmpById(int id) {
        return employeeRepository.getById(id);
    }

    @Override
    public void removeEmpById(int id) {
        employeeRepository.deleteById(id);
    }

    @Override
    public void removeEmp(Employee employee){
        employeeRepository.delete(employee);
    }

    @Override
    public void setEmpDao(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }
}
