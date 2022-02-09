package com.datagrokr.config;

import com.datagrokr.util.AwsSecretsManager;
import com.zaxxer.hikari.HikariDataSource;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
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

  @Autowired
  AwsSecretsManager awsSecretsManager;

  private static Logger logger = Logger.getLogger(DataSourceBasedMultiTenantConnectionProviderImpl.class.getName());

  private static final long serialVersionUID = 1L;

  private static final String DEFAULT_TENANT_ID = "persistence-tenant_emp_dev";

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
  private void readDataSource() {
      String tenantNames = "";
      if (env != null && env.getProperty("tenant-names") != null) {
        tenantNames = Objects.requireNonNull(env.getProperty("tenant-names"));
      }
      String[] tenantDbs = tenantNames.isEmpty() ? new String[0] : tenantNames.split(Pattern.quote("|"));

      try
      {
        // Fetch credentials from aws secret key manager
        String username = awsSecretsManager.username;
        String password = awsSecretsManager.password;

        for (String tenant : tenantDbs) {
          tenant = "persistence-" + tenant;
          String dummy = env.getProperty(tenant.concat(".url"));
          DataSource dsObject = DataSourceBuilder.create()
                  .username(username)
                  .password(password)
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
    catch(Exception e){
      logger.log(Level.SEVERE, "Encountered an exception :{0}", e.getMessage());
    }
  }

  private DataSource defaultDataSource() {
    try {
      // Fetch credentials from aws secret key manager
      String username = awsSecretsManager.username;
      String password = awsSecretsManager.password;

      DataSource dsObject = DataSourceBuilder.create()
              .username(username)
              .password(password)
              .url(env.getProperty(DEFAULT_TENANT_ID +".url"))
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
    catch(Exception e){
      logger.log(Level.SEVERE, "Encountered an exception :{0}", e.getMessage());
      return null;
    }
  }

  private String initializeTenantIfLost(String tenantIdentifier) {
    if (tenantIdentifier.equals(DbContextHolder.getCurrentDb())) {
      tenantIdentifier = DbContextHolder.getCurrentDb();
    }
    return tenantIdentifier;
  }
}
