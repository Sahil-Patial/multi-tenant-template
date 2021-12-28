package com.datagrokr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Main class.
 *
 * @author sahil
 */
@SpringBootApplication
@Configuration
@ComponentScan
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
public class MultiTenantDbImpl2Application {
  public static void main(String[] args) {
    SpringApplication.run(MultiTenantDbImpl2Application.class, args);
  }
}
