package com.datagrokr.security;

import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.GetCredentialsForIdentityRequest;
import software.amazon.awssdk.services.cognitoidentity.model.GetCredentialsForIdentityResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;


public class GetCognitoUserCredentials {
    public GetCognitoUserCredentials(){

    }

    public void getCredsForIdentity(CognitoIdentityClient cognitoClient, String identityId){
        try {
            GetCredentialsForIdentityRequest getCredentialsForIdentityRequest = GetCredentialsForIdentityRequest.builder()
                    .identityId(identityId)
                    .build();

            GetCredentialsForIdentityResponse response = cognitoClient.getCredentialsForIdentity(getCredentialsForIdentityRequest);
            System.out.println(response.credentials().secretKey());
            System.out.println("Identity ID " + response.identityId() + ", Access key ID " + response.credentials().accessKeyId());

        } catch (CognitoIdentityProviderException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    public void listAllUserPoolClients(CognitoIdentityProviderClient cognitoClient, String userPoolId) {

        try {
            ListUserPoolClientsResponse response = cognitoClient.listUserPoolClients(ListUserPoolClientsRequest.builder()
                    .userPoolId(userPoolId)
                    .build());

            response.userPoolClients().forEach(userPoolClient -> {
                        System.out.println("User pool client " + userPoolClient.clientName() + ", Pool ID " + userPoolClient.userPoolId() + ", Client ID " + userPoolClient.clientId() );
                    }
            );

        } catch (CognitoIdentityProviderException e){
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void createNewUser(CognitoIdentityProviderClient cognitoClient,
                                     String userPoolId,
                                     String name,
                                     String email,
                                     String password){

        try{

            AttributeType userAttrs = AttributeType.builder()
                    .name("email")
                    .value(email)
                    .build();

            AdminCreateUserRequest userRequest = AdminCreateUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(name)
                    .temporaryPassword(password)
                    .userAttributes(userAttrs)
                    .messageAction("SUPPRESS")
                    .build() ;

            AdminCreateUserResponse response = cognitoClient.adminCreateUser(userRequest);
            System.out.println("User " + response.user().username() + "is created. Status: " + response.user().userStatus());

        } catch (CognitoIdentityProviderException e){
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
