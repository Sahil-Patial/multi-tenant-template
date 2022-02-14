package com.datagrokr.controller;

import com.datagrokr.config.MultiTenantConfig;
import com.datagrokr.model.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

@TestPropertySource(locations="classpath:persistence-generic-entity.properties")
@SpringBootTest(classes = MultiTenantConfig.class, webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
public class EmployeeControllerTests {
    @Autowired
    EmployeeController employeeController;

    ObjectMapper objectMapper = new ObjectMapper();

    EmployeeControllerTests(){}

    @Test
    public void testFindAll() throws JsonProcessingException {
        ResponseEntity<?> response = employeeController.findAll("dev");
        List<Employee> actualResponse = objectMapper.readValue((String) response.getBody(), new TypeReference<List<Employee>>() {
        });

        Assertions.assertThat(response.getStatusCode()== HttpStatus.OK && actualResponse.size()>0);
    }

    @Test
    public void testFindAll_WithWrongEnvironment() throws JsonProcessingException {
        ResponseEntity<?> response = employeeController.findAll("dev1");

        Assertions.assertThat(response.getStatusCode()==HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testSave() throws JsonProcessingException {
        Employee entity = new Employee();
        entity.setEmpName("testEmp1");
        ResponseEntity<?> response = employeeController.save("dev", entity);
        Employee actualResponse = objectMapper.readValue((String) response.getBody(), Employee.class);

        Assertions.assertThat(response.getStatusCode()==HttpStatus.CREATED && actualResponse.empId==entity.empId);
    }

    @Test
    public void testSave_WithWrongEnvironment() throws JsonProcessingException {
        Employee entity = new Employee();
        entity.setEmpName("testEmp1");
        ResponseEntity<?> response = employeeController.save("dev1", entity);

        Assertions.assertThat(response.getStatusCode()==HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
