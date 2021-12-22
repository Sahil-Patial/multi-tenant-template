package com.multiTenantDbImpl2.repository;

import com.multiTenantDbImpl2.repository.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
}
