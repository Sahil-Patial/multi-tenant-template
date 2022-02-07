package com.datagrokr;

import com.datagrokr.config.MultiTenantConfig;
import com.datagrokr.controller.EmployeeController;
import com.datagrokr.model.Employee;
import com.datagrokr.repository.EmployeeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@TestPropertySource(locations="classpath:persistence-generic-entity.properties")
@SpringBootTest(classes = MultiTenantConfig.class, webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MultiTenantDbImpl2ApplicationTests {
  
  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private TestRestTemplate restTemplate;

  ObjectMapper mapper = new ObjectMapper();

  @Value("${local.server.port}")
  int randomServerPort;

  @Test
  @Order(1)
  public void testSaveEmployee() {
    
    Employee e = new Employee("test-emp1");
    Employee savedEmployee = employeeRepository.save(e);
    Assertions.assertThat(e.getEmpId()).isGreaterThan(0);
    //Assertions.assertThat(savedEmployee.getEmpId()).isEqualTo(e.getEmpId());
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

  @Test
  @Order(5)
  public void saveEmployeeWithHeaders() throws URISyntaxException {
    final String baseUrl = "http://localhost:"+randomServerPort+"/api/service/employee/save";
    URI uri = new URI(baseUrl);
    Employee employee = new Employee("TestEmp1");

    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", String.valueOf(MediaType.APPLICATION_JSON));
    headers.set("Accept", String.valueOf(MediaType.APPLICATION_JSON));
    headers.set("X-TENANT-ID", "test");

    HttpEntity<Employee> request = new HttpEntity<>(employee, headers);

    ResponseEntity<String> result = this.restTemplate.postForEntity(uri, request, String.class);

    //Employee resultEmployee = RetrieveUtil.retrieveResourceFromResponse(result, Employee.class);
    //Verify request succeed
    Assertions.assertThat( result.getStatusCode()== HttpStatus.CREATED );
  }
}
