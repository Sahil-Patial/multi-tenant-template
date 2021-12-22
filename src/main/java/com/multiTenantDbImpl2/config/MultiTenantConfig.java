package com.multiTenantDbImpl2.config;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@ComponentScan(basePackages = { "com.multiTenantDbImpl2.*" })
@EntityScan("com.multiTenantDbImpl2.*")
@EnableJpaRepositories(basePackages = "com.multiTenantDbImpl2.*",
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

    @Bean
    @ConditionalOnBean(name = "datasourceBasedMultitenantConnectionProvider")
    public LocalContainerEntityManagerFactoryBean userEntityManager(
            @Qualifier("datasourceBasedMultitenantConnectionProvider")
                MultiTenantConnectionProvider connectionProvider,
            @Qualifier("currentTenantIdentifierResolver")
                CurrentTenantIdentifierResolver tenantResolver) {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPackagesToScan("com.multiTenantDbImpl2.repository.model");

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        final HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put(org.hibernate.cfg.Environment.MULTI_TENANT, MultiTenancyStrategy.SCHEMA);
        String tenant = DbContextHolder.getCurrentDb();
        properties.put(org.hibernate.cfg.Environment.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
        properties.put(org.hibernate.cfg.Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantResolver);
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
        em.setJpaPropertyMap(properties);

        return em;
    }

    //@Bean
    @PostConstruct
    public DataSource userDataSource() {
        String tenantName = "";
        if(tenantName.isEmpty() || tenantName.isBlank())
            tenantName = "persistence-tenant_emp_default";

        return DataSourceBuilder.create()
                .username(env.getProperty(tenantName.concat(".username")))
                .password(env.getProperty(tenantName.concat(".password")))
                .url(env.getProperty(tenantName.concat(".url")))
                .driverClassName(env.getProperty("spring.datasource.driver-class-name"))
                .build();
    }

    @Bean
    public PlatformTransactionManager userTransactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(userEntityManager(
                new DataSourceBasedMultiTenantConnectionProviderImpl(),
                new CurrentTenantIdentifierResolverImpl()).getObject()
        );
        return transactionManager;
    }
}
