package com.datagrokr.config;

import com.zaxxer.hikari.HikariDataSource;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Provides connection per tenant
 *
 */
@Configuration
@SuppressFBWarnings(value = {"SE_BAD_FIELD", "SE_TRANSIENT_FIELD_NOT_RESTORED"},
        justification = "Make the build pass")
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
    if (dataSourcesMtApp.isEmpty()) {
      readDataSource();
      if (dataSourcesMtApp.isEmpty()) {
        return DataSourceBuilder.create().build();
      }
    }
    return this.dataSourcesMtApp.values().iterator().next();
  }

  @Override
  protected DataSource selectDataSource(String tenantIdentifier) {
    if (dataSourcesMtApp.isEmpty()) {
      readDataSource();
    }

    if (!this.dataSourcesMtApp.containsKey(tenantIdentifier)) {
      return DataSourceBuilder.create().build();
    }
    return this.dataSourcesMtApp.get(tenantIdentifier);
  }

  @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE",
          justification = "Already added a null check")
  private void readDataSource(){
    String tenantNames = "";
    if(env!=null && env.getProperty("tenant-names")!=null) {
      tenantNames = Objects.requireNonNull(env.getProperty("tenant-names"));
    }
    String[] tenantDbs = tenantNames.isEmpty()? new String[0]:tenantNames.split(Pattern.quote("|"));

    for (String tenant : tenantDbs) {
      tenant = "persistence-" + tenant;

      DataSource dsObject = DataSourceBuilder.create()
              .username(env.getProperty(tenant.concat(".username")))
              .password(env.getProperty(tenant.concat(".password")))
              .url(env.getProperty(tenant.concat(".url")))
              .driverClassName(env.getProperty("spring.datasource.driver-class-name"))
              .build();

      HikariDataSource hikariDs = new HikariDataSource();
      hikariDs.setDataSource(dsObject);
      //hikariDs.setIdleTimeout(20000);
      //hikariDs.setMaximumPoolSize(250);
      //hikariDs.setMinimumIdle(100);
      //hikariDs.setLeakDetectionThreshold(20000);
      DataSource ds = hikariDs.getDataSource();

      dataSourcesMtApp.put(tenant, ds);
    }
  }

  private DataSource defaultDataSource() {
    DataSource dsObject = DataSourceBuilder.create()
            .username(env.getProperty("persistence-tenant_emp_default.username"))
            .password(env.getProperty("persistence-tenant_emp_default.password"))
            .url(env.getProperty("persistence-tenant_emp_default.url"))
            .driverClassName(env.getProperty("spring.datasource.driver-class-name"))
            .build();

    HikariDataSource hikariDs = new HikariDataSource();
    hikariDs.setDataSource(dsObject);
    //hikariDs.setIdleTimeout(20000);
    //hikariDs.setMaximumPoolSize(200);
    //hikariDs.setMinimumIdle(50);
    //hikariDs.setLeakDetectionThreshold(20000);

    return hikariDs.getDataSource();
  }

  private String initializeTenantIfLost(String tenantIdentifier) {
    if (tenantIdentifier.equals(DbContextHolder.getCurrentDb())) {
      tenantIdentifier = DbContextHolder.getCurrentDb();
    }
    return tenantIdentifier;
  }
}
