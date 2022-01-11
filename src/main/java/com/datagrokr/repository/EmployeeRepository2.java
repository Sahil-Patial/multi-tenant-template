package com.datagrokr.repository;

import com.datagrokr.controller.EmployeeController;
import com.datagrokr.repository.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

//@Repository
public class EmployeeRepository2 {
    @Autowired
    private LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;

    private static Logger logger = Logger.getLogger(EmployeeController.class.getName());

    private EntityManager em;

    public EmployeeRepository2(){
        EntityManagerFactory emf = localContainerEntityManagerFactoryBean.getObject();
        em = emf.createEntityManager();
    }

    public List<Employee> findAll(){
        try{
            em.getTransaction().begin();
            return em.createQuery("from employee").getResultList();
        }
        catch (Exception e){
            logger.log(Level.SEVERE,
                    "Exiting from {0} with an error {1}", new Object[]{"findAll()", e});
            return null;
        }
    }

    public Optional<Employee> save(Employee entity){
        try{
            em.getTransaction().begin();
            em.persist(entity);
            return Optional.of(entity);
        }
        catch(Exception e){
            logger.log(Level.SEVERE,
                    "Exiting from {0} with an error {1}", new Object[]{"save()", e});
            return Optional.empty();
        }
    }
}
