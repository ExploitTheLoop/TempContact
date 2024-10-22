package com.animetone.incognitocontact.security;

public class Authenticator {
    private static boolean authVar = false; // Default to false

    // Method to get the value of authVar
    public static boolean isAuthenticated() {
        return authVar;
    }

    // Method to set the value of authVar
    public static void setAuthenticated(boolean value) {
        authVar = value;
    }
}


