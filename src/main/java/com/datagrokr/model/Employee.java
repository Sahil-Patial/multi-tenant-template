package com.datagrokr.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity class.
 *
 * @author sahil
 */
@Entity
@Table(name = "employee")
public class Employee extends BaseEntity implements Serializable {
  @Id
  @Column(name = "emp_id")
  @SequenceGenerator(name = "emp_id_sequence", sequenceName = "public.employee_seq", allocationSize =1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "emp_id_sequence")
  public Integer empId;
  
  @Column(name = "emp_name")
  public String empName;
  
  public Employee(){}
  
  public Employee(String empName){
    this.empName = empName;
  }
  
  public Integer getEmpId() {
    return empId;
  }
  
  public void setEmpId(Integer empId) {
    this.empId = empId;
  }
  
  public String getEmpName() {
    return empName;
  }
  
  public void setEmpName(String empName) {
    this.empName = empName;
  }
}
