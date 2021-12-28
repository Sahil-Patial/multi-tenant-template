package com.datagrokr.repository.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Integer empId;
  
  @Column(name = "emp_name")
  public String empName;
}
