package com.datagrokr;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.LogManager;

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
  @Autowired
  private Environment env;

  public static void main(String[] args) throws IOException {
    // Setting logging config file location
    String localDir = System.getProperty("user.dir");
    System.setProperty("java.util.logging.config.file", localDir+ "\\src\\main\\resources\\logging-default.properties");

    SpringApplication.run(MultiTenantDbImpl2Application.class, args);
  }
}
