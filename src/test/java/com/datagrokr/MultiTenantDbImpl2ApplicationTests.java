package com.datagrokr;

import com.datagrokr.model.Employee;
import com.datagrokr.repository.EmployeeRepository;
import config.JpaConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = JpaConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MultiTenantDbImpl2ApplicationTests {
  
  @Autowired
  private EmployeeRepository employeeRepository;

  @Test
  @Order(1)
  public void testSaveEmployee() {
    
    Employee e = new Employee("test-emp1");
    employeeRepository.save(e);    
    Assertions.assertThat(e.getEmpId()).isGreaterThan(0);
  }
  
  @Test
  @Order(2)
  public void testGetEmployee() {
    
    Employee e = employeeRepository.findById(1).get();
    Assertions.assertThat(e.getEmpId()).isEqualTo(1);
  }
  
  @Test
  @Order(3)
  public void testUpdateEmployee() {
  
    Employee e = employeeRepository.findByEmpName("test-emp1");
    e.setEmpName("test-emp2");
    
    Employee eupdated = employeeRepository.save(e);
    Assertions.assertThat(eupdated.getEmpName()).isEqualTo("test-emp2");
  }
  
  @Test
  @Order(4)
  public void testDeleteEmployee() {
    
    Employee e = employeeRepository.findByEmpName("test-emp2");
    employeeRepository.delete(e);
    Employee deletedEmployee = employeeRepository.findByEmpName("test-emp2");
    Assertions.assertThat(deletedEmployee).isNull();    
  }

}
