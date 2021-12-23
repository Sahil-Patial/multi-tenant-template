package com.multiTenantDbImpl2.controller;

import com.multiTenantDbImpl2.config.DbContextHolder;
import com.multiTenantDbImpl2.repository.EmployeeRepository;
import com.multiTenantDbImpl2.repository.model.Employee;
//import com.multiTenantDbImpl2.service.impl.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/service/employee")
public class EmployeeController {
//    @Autowired
//    private EmployeeService employeeService;
    
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ApplicationContext applicationContext;
    
    private static Logger logger = Logger.getLogger(EmployeeController.class.getName());

    public EmployeeController(){}

    @GetMapping("/findAll")
    public String findAll(@RequestHeader(name = "X-TENANT-ID") String env) { //  See Headers
        try {
            String tenantStr = "persistence-tenant_emp_" + env;
            StringBuilder responseStr = new StringBuilder();
            DbContextHolder.setCurrentDb(tenantStr);

            List<Employee> employeeList = employeeRepository.findAll();
            for(Employee e : employeeList){
                responseStr.append(e.empId + " |" + e.empName + System.lineSeparator());
            }
            
            logger.log(Level.INFO, "Employee details were retrieved using GET method from {0} tenant", env);
            
            System.out.println(responseStr.toString());
          
            return responseStr.toString();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return "Failure";
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestHeader(name = "X-TENANT-ID") String env, @RequestBody Employee employee ) { //  See Headers
        final String methodName = "save()";
        logger.log(Level.FINER, "Entering {}", methodName);
        try {
            String tenantStr = "persistence-tenant_emp_" + env;
            DbContextHolder.setCurrentDb(tenantStr);

            Employee empResponse = employeeRepository.save(employee);
            
            logger.log(Level.INFO, "Employee details were stored using POST method from {0} tenant", env);
            
            logger.log(Level.FINER, "Exiting {}", methodName);
            return new ResponseEntity<>(empResponse, HttpStatus.CREATED);
        }
        catch(Exception e){
            logger.log(Level.SEVERE, "Exiting from {0} with an error {1}",new Object[]{methodName, e});
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
