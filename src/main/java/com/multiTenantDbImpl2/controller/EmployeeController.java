package com.multiTenantDbImpl2.controller;

import com.multiTenantDbImpl2.config.DbContextHolder;
import com.multiTenantDbImpl2.repository.model.Employee;
import com.multiTenantDbImpl2.service.impl.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ApplicationContext applicationContext;

    public EmployeeController(){}

    @GetMapping("/getEmp")
    public String connectToDb(@RequestHeader(name = "X-TENANT-ID") String env) { //  See Headers
        try {
            String tenantStr = "persistence-tenant_emp_" + env;
            StringBuilder responseStr = new StringBuilder();
            DbContextHolder.setCurrentDb(tenantStr);

            List<Employee> employeeList = employeeService.listEmp();
            for(Employee e : employeeList){
                responseStr.append(e.empId + " |" + e.empName + System.lineSeparator());
            }
            System.out.println(responseStr.toString());

            return responseStr.toString();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return "Failure";
        }
    }

//    @PostMapping("/saveEmp")
//    public ResponseEntity<?> createEmp(@RequestHeader(name = "X-TENANT-ID") @RequestBody Employee employee){
//
//        Employee empResponse = employeeService.addEmp(employee);
//
//        return new ResponseEntity<>(empResponse, HttpStatus.CREATED);
//    }

    @PostMapping("/saveEmp")
    public ResponseEntity<?> createEmp(@RequestHeader(name = "X-TENANT-ID") String env, @RequestBody Employee employee ) { //  See Headers
        try {
            String tenantStr = "persistence-tenant_emp_" + env;
            StringBuilder responseStr = new StringBuilder();
            DbContextHolder.setCurrentDb(tenantStr);

            Employee empResponse = employeeService.addEmp(employee);

            System.out.println(responseStr.toString());

//            return responseStr.toString();
            return new ResponseEntity<>(empResponse, HttpStatus.CREATED);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
