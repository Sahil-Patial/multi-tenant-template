package com.datagrokr.controller;

import com.datagrokr.config.DbContextHolder;
import com.datagrokr.repository.EmployeeRepository;
import com.datagrokr.repository.model.Employee;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

/**
 * Controller class with rest endpoints.
 *
 * @author sahil
 */
@RestController
@RequestMapping("/api/service/employee")
public class EmployeeController {
    
  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private ApplicationContext applicationContext;

  private static Logger logger = Logger.getLogger(EmployeeController.class.getName());

  public EmployeeController() {}
  
  /**
   * Get method to fetch all data.
   *
   * @param env tenants
   * @return data or error
   */
  @GetMapping("/findAll")
  public String findAll(@RequestHeader(name = "X-TENANT-ID") String env) { //  See Headers
    final String methodName = "findAll()";
    logger.log(Level.FINER, "Entering {}", methodName);
    try {
      String tenantStr = "persistence-tenant_emp_" + env;
      StringBuilder responseStr = new StringBuilder();
      DbContextHolder.setCurrentDb(tenantStr);

      List<Employee> employeeList = employeeRepository.findAll();
      for (Employee e : employeeList) {
        responseStr.append(e.empId + " |" + e.empName + System.lineSeparator());
      }

      logger.log(Level.INFO, 
              "Employee details were retrieved using GET method from {0} tenant", env);
      logger.log(Level.FINER, "Exiting {}", methodName);

      System.out.println(responseStr.toString());

      return responseStr.toString();
    } catch (Exception e) {
      logger.log(Level.SEVERE, 
              "Exiting from {0} with an error {1}", new Object[]{methodName, e});
      return "Failure";
    }
    finally {

    }
  }
  
  /**
   * Post method to save data.
   *
   * @param env tenants
   * @param employee data to be saved
   * @return data or error
   */
  @PostMapping("/save")
  public ResponseEntity<?> save(@RequestHeader(name = "X-TENANT-ID") String env, 
          @RequestBody Employee employee) { //  See Headers
    final String methodName = "save()";
    logger.log(Level.FINER, "Entering {}", methodName);
    try {
      String tenantStr = "persistence-tenant_emp_" + env;
      DbContextHolder.setCurrentDb(tenantStr);

      Employee empResponse = employeeRepository.save(employee);

      logger.log(Level.INFO, 
              "Employee details were stored using POST method from {0} tenant", env);
      logger.log(Level.FINER, "Exiting {}", methodName);

      return new ResponseEntity<>(empResponse, HttpStatus.CREATED);
    } catch (Exception e) {
      logger.log(Level.SEVERE, 
              "Exiting from {0} with an error {1}", new Object[]{methodName, e});
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
