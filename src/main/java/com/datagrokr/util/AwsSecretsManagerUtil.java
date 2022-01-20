package com.datagrokr.util;

import com.amazonaws.auth.*;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import io.github.cdimascio.dotenv.Dotenv;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AwsSecretsManagerUtil {

    private static Logger logger = Logger.getLogger(AwsSecretsManagerUtil.class.getName());

    public String fetchDBCreds(String secretName) throws Exception {

        String endpoint = "secretsmanager.us-east-2.amazonaws.com";
        String region = "us-east-2";
        Dotenv dotenv = Dotenv.configure().load();
        String awsAccessKey = dotenv.get("AWS_ACCESS_KEY");
        String awsSecretKey = dotenv.get("AWS_SECRET_KEY");

        AwsClientBuilder.EndpointConfiguration config = new AwsClientBuilder.EndpointConfiguration(endpoint, region);
        AWSSecretsManagerClientBuilder clientBuilder = AWSSecretsManagerClientBuilder.standard();
        clientBuilder.setEndpointConfiguration(config);

        AWSSecretsManager client = clientBuilder
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey)))
                .build();

        String secret;
        ByteBuffer binarySecretData;
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