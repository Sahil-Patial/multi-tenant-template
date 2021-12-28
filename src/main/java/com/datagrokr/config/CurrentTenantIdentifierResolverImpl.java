package com.datagrokr.config;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

/**
 * It resolves the tenant identifier to use.
 *
 * @author sahil
 */
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

  private static final String DEFAULT_TENANT_ID = "persistence-tenant_emp_default";
  
  @Override
    public String resolveCurrentTenantIdentifier() {
    String tenant = DbContextHolder.getCurrentDb();
    if (tenant == null) {
      tenant = DEFAULT_TENANT_ID;
    }
    return tenant.isEmpty() || tenant.isBlank() ? DEFAULT_TENANT_ID : tenant;
  }

  @Override
  public boolean validateExistingCurrentSessions() {
    return true;
  }
}
