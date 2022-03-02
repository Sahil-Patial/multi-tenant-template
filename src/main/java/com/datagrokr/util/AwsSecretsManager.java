package com.datagrokr.util;

import com.amazonaws.auth.*;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class AwsSecretsManager {
    private Environment env;

    private static Logger logger = Logger.getLogger(AwsSecretsManager.class.getName());

    private String endpoint = "secretsmanager.us-east-2.amazonaws.com";
    private String region = "us-east-2";

    public final String username;
    public final String password;

    @Autowired
    public AwsSecretsManager(Environment env) throws Exception {
        this.env = env;
        JSONObject obj = new JSONObject(fetchDBCreds(env.getProperty("secret-name")));

        username = obj.getString("username");
        password = obj.getString("password");
    }

    public String fetchDBCreds(String secretName) throws Exception {
        Dotenv dotenv = Dotenv.configure().load();
        String awsAccessKey = dotenv.get("AWS_ACCESS_KEY_ID");
        String awsSecretKey = dotenv.get("AWS_SECRET_KEY");

        AwsClientBuilder.EndpointConfiguration config = new AwsClientBuilder.EndpointConfiguration(endpoint, region);
        AWSSecretsManagerClientBuilder clientBuilder = AWSSecretsManagerClientBuilder.standard();
        clientBuilder.setEndpointConfiguration(config);

        AWSSecretsManager client = clientBuilder
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey)))
                .build();

        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(secretName);
        GetSecretValueResult getSecretValueResult = null;
        try {
            getSecretValueResult = client.getSecretValue(getSecretValueRequest);

        } catch(Exception e) {
            logger.log(Level.SEVERE,"The requested secret " + secretName + " was not found");
            e.printStackTrace();
        }

        if(getSecretValueResult == null) {
            logger.log(Level.SEVERE, "The requested secret " + secretName + " was empty");
        }

        // Depending on whether the secret was a string or binary, one of these fields will be populated
        if(getSecretValueResult.getSecretString() != null) {
            return getSecretValueResult.getSecretString();
        }
        else {
            logger.log(Level.SEVERE, "Exception thrown. Secrets manager read failed.");
            throw new Exception("Secrets manager read failed.");
        }
    }
}
