package com.datagrokr.config;

import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Handles db properties based on tenant-id.
 *
 * @author sahil
 */
@Configuration
@ComponentScan(basePackages = { "com.datagrokr.*" })
@EntityScan("com.datagrokr.*")
@EnableJpaRepositories(basePackages = "com.datagrokr.*",
        entityManagerFactoryRef = "userEntityManager",
        transactionManagerRef = "userTransactionManager")
public class MultiTenantConfig {
  @Autowired
  private Environment env;

  @Autowired
  private ApplicationContext applicationContext;

  public MultiTenantConfig() {
    super();
  }

  @Bean(name = "datasourceBasedMultitenantConnectionProvider")
  public MultiTenantConnectionProvider multiTenantConnectionProvider() {
    return new DataSourceBasedMultiTenantConnectionProviderImpl();
  }

  @Bean(name = "currentTenantIdentifierResolver")
  public CurrentTenantIdentifierResolver currentTenantIdentifierResolver() {
    return new CurrentTenantIdentifierResolverImpl();
  }
  
  /**
   * Creates the entity manager factory bean which is required to access the
   * JPA functionalities provided by the JPA persistence provider, i.e.
   * Hibernate in this case.
   *
   * @param connectionProvider provides connection
   * @param tenantResolver resolves tenant
   * @return em
   */
  @Bean
  @ConditionalOnBean(name = "datasourceBasedMultitenantConnectionProvider")
  public LocalContainerEntityManagerFactoryBean userEntityManager(
          @Qualifier("datasourceBasedMultitenantConnectionProvider")
              MultiTenantConnectionProvider connectionProvider,
          @Qualifier("currentTenantIdentifierResolver")
              CurrentTenantIdentifierResolver tenantResolver) {
    final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setPackagesToScan("com.datagrokr.model");

    final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);
    final HashMap<String, Object> properties = new HashMap<String, Object>();
    properties.put(org.hibernate.cfg.Environment
            .MULTI_TENANT, MultiTenancyStrategy.SCHEMA);

    properties.put(org.hibernate.cfg.Environment
            .MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
    properties.put(org.hibernate.cfg.Environment
            .MULTI_TENANT_IDENTIFIER_RESOLVER, tenantResolver);
    properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
    properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));

    em.setJpaPropertyMap(properties);

    return em;
  }
  
  /**
   * Handles transaction.
   *
   * @return object
   */
  @Bean
  public PlatformTransactionManager userTransactionManager() {
    final JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(userEntityManager(
            (DataSourceBasedMultiTenantConnectionProviderImpl)applicationContext.getBean("datasourceBasedMultitenantConnectionProvider"),
            (CurrentTenantIdentifierResolverImpl)applicationContext.getBean("currentTenantIdentifierResolver")).getObject()
    );
    return transactionManager;
  }
}

//  /**
//   * Creates DataSources.
//   *
//   * @return properties
//   */
//  @PostConstruct
//  public DataSource userDataSource() {
//    String tenantName = "";
//    if (tenantName.isEmpty() || tenantName.isBlank()) {
//      tenantName = "persistence-tenant_emp_default";
//    }
//
//    return DataSourceBuilder.create()
//            .username(env.getProperty(tenantName.concat(".username")))
//            .password(env.getProperty(tenantName.concat(".password")))
//            .url(env.getProperty(tenantName.concat(".url")))
//            .driverClassName(env.getProperty("spring.datasource.driver-class-name"))
//            .build();
//  }