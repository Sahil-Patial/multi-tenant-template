package com.multiTenantDbImpl2.config;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.*;
import java.util.regex.Pattern;

@Configuration
public class DataSourceBasedMultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {
    @Autowired
    private Environment env;

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceBasedMultiTenantConnectionProviderImpl.class);

    private static final long serialVersionUID = 1L;

    private transient HashMap<String, DataSource> dataSourcesMtApp = new HashMap<>();

    DataSourceBasedMultiTenantConnectionProviderImpl() {
    }

    @Override
    protected DataSource selectAnyDataSource() {
        readDataSource();
        if (dataSourcesMtApp.isEmpty()) {
            return defaultDataSource();
        }
        return this.dataSourcesMtApp.values().iterator().next();
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        readDataSource();

        if (!this.dataSourcesMtApp.containsKey(tenantIdentifier)) {
            return defaultDataSource();
        }
        return this.dataSourcesMtApp.get(tenantIdentifier);
    }

    private void readDataSource(){
        String tenantNames = new String();
        if(env!=null && env.getProperty("tenant-names")!=null) {
            tenantNames = Objects.requireNonNull(env.getProperty("tenant-names"));
        }
        String[] tenantDbs = tenantNames.isEmpty()? new String[0]:tenantNames.split(Pattern.quote("|"));

        for (String tenant : tenantDbs) {
            tenant = "persistence-" + tenant;
            DataSource ds = DataSourceBuilder.create()
                    .username(env.getProperty(tenant.concat(".username")))
                    .password(env.getProperty(tenant.concat(".password")))
                    .url(env.getProperty(tenant.concat(".url")))
                    .driverClassName(env.getProperty("spring.datasource.driver-class-name"))
                    .build();
            dataSourcesMtApp.put(tenant, ds);
        }
    }

    private DataSource defaultDataSource() {
        return DataSourceBuilder.create()
                .username(env.getProperty("persistence-tenant_emp_default.username"))
                .password(env.getProperty("persistence-tenant_emp_default.password"))
                .url(env.getProperty("persistence-tenant_emp_default.url"))
                .driverClassName(env.getProperty("spring.datasource.driver-class-name"))
                .build();
    }

    private String initializeTenantIfLost(String tenantIdentifier) {
        if (tenantIdentifier.equals(DbContextHolder.getCurrentDb())) {
            tenantIdentifier = DbContextHolder.getCurrentDb();
        }
        return tenantIdentifier;
    }
}
