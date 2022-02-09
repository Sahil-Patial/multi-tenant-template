package com.datagrokr.config;

/**
 * The context holder implementation is a container that stores 
 * the current context as a ThreadLocal reference.
 *
 * @author sahil
 */
public class DbContextHolder {
  private static String DEFAULT_TENANT_ID = "dev";
  private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

  static {
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