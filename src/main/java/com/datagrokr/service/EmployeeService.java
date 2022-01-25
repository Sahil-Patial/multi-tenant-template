package com.datagrokr.service;

import com.datagrokr.model.Employee;
import com.datagrokr.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmployeeService implements IEmployeeService {
    @Autowired
    EmployeeRepository employeeRepository;

    private static Logger logger = Logger.getLogger(EmployeeService.class.getName());

    public EmployeeService(){}

    public List<Employee> findAll(){
        try {
            return employeeRepository.findAll();
        }
        catch(Exception e){
            logger.log(Level.SEVERE,
                    "Exiting from {0} with an error {1}", new Object[]{"findAll()", e});
            return Collections.EMPTY_LIST;
        }
    }

    public Optional<Employee> findById(int id){
        try{
            return employeeRepository.findById(id);
        }
        catch(Exception e){
            logger.log(Level.SEVERE,
                    "Exiting from {0} with an error {1}", new Object[]{"findById()", e});
            return null;
        }
    }

    public Employee save(Employee entity){
        try{
            return employeeRepository.save(entity);
        }
        catch (Exception e){
            logger.log(Level.SEVERE,
                    "Exiting from {0} with an error {1}", new Object[]{"save()", e});
            return new Employee();
        }
    }

    public Employee update(Employee entity){
        try{
            Optional<Employee> employee = Optional.ofNullable(employeeRepository.findById(entity.empId).orElseThrow(EntityNotFoundException::new));

            return employeeRepository.save(entity);
        }
        catch(ObjectOptimisticLockingFailureException e){
            logger.log(Level.SEVERE,
                    "Somebody has already updated the amount for employeeId:{} in concurrent transaction. Will try again",
                        entity.empId);
            return employeeRepository.save(entity);
        }
        catch(Exception e){
            logger.log(Level.SEVERE,
                    "Exiting from {0} with an error {1}", new Object[]{"update()", e});
            return new Employee();
        }
    }
}
