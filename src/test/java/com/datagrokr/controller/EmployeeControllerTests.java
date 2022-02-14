package com.datagrokr.controller;

import com.datagrokr.config.MultiTenantConfig;
import com.datagrokr.model.Employee;
import com.datagrokr.repository.EmployeeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations="classpath:persistence-generic-entity.properties")
@SpringBootTest(classes = MultiTenantConfig.class, webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
public class EmployeeControllerTests {
    @InjectMocks
    private EmployeeController employeeController;

    @Mock
    private EmployeeRepository employeeRepository;

    public static List<Employee> listOfEmployees = new ArrayList<Employee>();

    /**
     * Setup resources for test
     */
    @BeforeAll
    public static void setup(){
        listOfEmployees.add(new Employee("testEmp1"));
        listOfEmployees.add(new Employee("testEmp2"));
        listOfEmployees.add(new Employee("testEmp3"));
    }

    /**
     * Delete or release resources
     */
    @AfterAll
    public static void tearDown(){
        listOfEmployees.removeAll(listOfEmployees);
    }

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testFindAll() throws JsonProcessingException {
        when(employeeRepository.findAll()).thenReturn(listOfEmployees);
        ResponseEntity<?> response = employeeController.findAll("dev");
        List<Employee> actualResponse = objectMapper.readValue((String) response.getBody(), new TypeReference<List<Employee>>() {
        });

        Assertions.assertThat(response.getStatusCode()== HttpStatus.OK && actualResponse.size()>0);
    }

    /**
     * This test is meaningful only for integration test, as repository is mocked otherwise.
     * @throws JsonProcessingException
     */
    @Test
    public void testFindAll_WithWrongEnvironment() throws JsonProcessingException {
        //when(employeeRepository.findAll()).thenReturn(listOfEmployees);
        ResponseEntity<?> response = employeeController.findAll("dev1");

        Assertions.assertThat(response.getStatusCode()==HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testSave() throws JsonProcessingException {
        Employee entity = new Employee();
        entity.setEmpName("testEmp1");
        when(employeeRepository.save(entity)).thenReturn(entity);

        ResponseEntity<?> response = employeeController.save("dev", entity);
        Employee actualResponse = objectMapper.readValue((String) response.getBody(), Employee.class);

        Assertions.assertThat(response.getStatusCode()==HttpStatus.CREATED && actualResponse.empId==entity.empId);
    }

    /**
     * This test is meaningful only for integration test, as repository is mocked otherwise.
     * @throws JsonProcessingException
     */
    @Test
    public void testSave_WithWrongEnvironment() throws JsonProcessingException {
        Employee entity = new Employee();
        entity.setEmpName("testEmp1");
        //when(employeeRepository.save(entity)).thenReturn(entity);

        ResponseEntity<?> response = employeeController.save("dev1", entity);

        Assertions.assertThat(response.getStatusCode()==HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
