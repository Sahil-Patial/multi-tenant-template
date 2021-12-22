package com.multiTenantDbImpl2.config;

import org.springframework.context.annotation.Configuration;

public class DbContextHolder {
    private static String DEFAULT_TENANT_ID = "persistence-tenant_emp_default";
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    static{
        DbContextHolder.setCurrentDb(DEFAULT_TENANT_ID);
    }

    public static void setCurrentDb(String dbType) {
        contextHolder.set(dbType);
    }

    public static String getCurrentDb() {
        return contextHolder.get();
    }

    public static void clear() {
        contextHolder.remove();
    }
}