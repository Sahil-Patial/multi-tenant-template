package com.multiTenantDbImpl2.service;

import com.multiTenantDbImpl2.repository.model.Employee;
import com.multiTenantDbImpl2.repository.EmployeeRepository;

import java.util.List;

public interface IEmployeeService {
    Employee addEmp(Employee input);

    void updateEmp(Employee input);

    List<Employee> listEmp();

    Employee getEmpById(int id);

    void removeEmpById(int id);

    void removeEmp(Employee employee);

    void setEmpDao(EmployeeRepository bookDao);
}
