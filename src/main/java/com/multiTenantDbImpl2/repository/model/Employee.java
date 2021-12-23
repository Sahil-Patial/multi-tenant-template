package com.multiTenantDbImpl2.repository.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "employee")
public class Employee extends BaseEntity implements Serializable {
    @Id
    @Column(name="emp_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer empId;
    @Column(name="emp_name")
    public String empName;
}
