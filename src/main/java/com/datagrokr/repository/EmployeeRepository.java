package com.datagrokr.repository;

import com.datagrokr.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository class containing various JPA crud methods.
 *
 * @author sahil
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
  public Employee findByEmpName(String empName);
}
