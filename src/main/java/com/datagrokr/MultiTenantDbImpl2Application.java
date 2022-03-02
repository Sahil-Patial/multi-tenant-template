package com.datagrokr;

import com.datagrokr.security.GetCognitoUserCredentials;
import com.datagrokr.util.AwsSecretsManager;
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
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

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

    Dotenv dotenv = Dotenv.configure().load();
    System.setProperty("aws.accessKeyId", dotenv.get("AWS_ACCESS_KEY_ID"));
    System.setProperty("aws.secretAccessKey", dotenv.get("AWS_SECRET_KEY"));
    String identityId = "us-east-1:4a41aa9c-f71d-4c13-9764-8a71ce9118cb";
    String userPoolId = "us-east-1_tEr8oQfHF";
    CognitoIdentityClient cognitoClient = CognitoIdentityClient.builder()
            .region(Region.US_EAST_1)
            .build();
    CognitoIdentityProviderClient cognitoIdentityClient = CognitoIdentityProviderClient.builder()
            .region(Region.US_EAST_1)
            .build();

    GetCognitoUserCredentials getCognitoUserCredentials = new GetCognitoUserCredentials();
    //getCognitoUserCredentials.getCredsForIdentity(cognitoClient, identityId);
    getCognitoUserCredentials.listAllUserPoolClients(cognitoIdentityClient, userPoolId);
    cognitoClient.close();
    SpringApplication.run(MultiTenantDbImpl2Application.class, args);
  }
}
