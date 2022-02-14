package com.datagrokr.controller;

import com.datagrokr.config.DbContextHolder;
import com.datagrokr.repository.EmployeeRepository;
import com.datagrokr.model.Employee;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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

  private ObjectMapper objectMapper = new ObjectMapper();

  private static Logger logger = Logger.getLogger(EmployeeController.class.getName());

  public EmployeeController() {}
  
  /**
   * Get method to fetch all data.
   *
   * @param tenantEnv tenants
   * @return data or error
   */
  @GetMapping("/findAll")
  public ResponseEntity<?> findAll(@RequestHeader(name = "X-TENANT-ID") String tenantEnv) { //  See Headers
    final String methodName = "findAll()";
    logger.log(Level.FINER, "Entering {}", methodName);
    try {
      StringBuilder responseStr = new StringBuilder();
      DbContextHolder.setCurrentDb(tenantEnv);

      List<Employee> employeeList = Collections.EMPTY_LIST;
      try {
        employeeList = employeeRepository.findAll();
      }
      catch (OptimisticLockingFailureException e){
        logger.log(Level.SEVERE,
                "Some exception occured in concurrent transaction. Will try again");
        employeeList = employeeRepository.findAll();
      }
      responseStr.append(objectMapper.writeValueAsString(employeeList));

      logger.log(Level.INFO, 
              "Employee details were retrieved using GET method from {0} tenant", tenantEnv);
      logger.log(Level.FINER, "Exiting {}", methodName);

      System.out.println(responseStr.toString());

      return ResponseEntity.ok(responseStr.toString());
    }
    catch(OptimisticLockingFailureException e){
      logger.log(Level.SEVERE, "Something went wrong while processing the request. {}", e.toString());
      return new ResponseEntity<String>("Please try again. Concurrent request error.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, 
              "Exiting from {0} with an error {1}", new Object[]{methodName, e});
      return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
  
  /**
   * Post method to save data.
   *
   * @param tenantEnv tenants
   * @param employee data to be saved
   * @return data or error
   */
  @PostMapping("/save")
  public ResponseEntity<?> save(@RequestHeader(name = "X-TENANT-ID") String tenantEnv,
          @Valid @RequestBody Employee employee) { //  See Headers
    final String methodName = "save()";
    logger.log(Level.FINER, "Entering {}", methodName);
    try {
      StringBuilder responseStr = new StringBuilder();
      DbContextHolder.setCurrentDb(tenantEnv);
      Employee empResponse;

      try {
        empResponse = employeeRepository.save(employee);
      }
      catch(ObjectOptimisticLockingFailureException e) {
        logger.log(Level.SEVERE,
                "Somebody has already updated the amount for employeeId:{} in concurrent transaction. Will try again",
                employee.empId);
        empResponse = employeeRepository.save(employee);
      }
      logger.log(Level.INFO, 
              "Employee details were stored using POST method from {0} tenant", tenantEnv);
      logger.log(Level.FINER, "Exiting {}", methodName);

      responseStr.append(objectMapper.writeValueAsString(empResponse));
      return new ResponseEntity<>(responseStr.toString(), HttpStatus.CREATED);
    }
    catch(OptimisticLockingFailureException e){
      logger.log(Level.SEVERE, "Something went wrong while processing the request. {}", e.toString());
      return new ResponseEntity<String>("Please try again. Concurrent request error.", HttpStatus.CONFLICT);
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, 
              "Exiting from {0} with an error {1}", new Object[]{methodName, e});
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
