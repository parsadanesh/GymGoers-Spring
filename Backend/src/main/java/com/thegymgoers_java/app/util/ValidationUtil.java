package com.thegymgoers_java.app.util;

public class ValidationUtil {

    // Throws an exception if the username is null or empty
    public static void validateString(String string) {
        if (string == null || string.trim().isEmpty()) {
            throw new IllegalArgumentException("Details cannot not be empty or null");
        }
    }

    // Throws an exception if the user's email/username used to register is null or empty
    public static void validateUserDetails(String username, String emailAddress) {
        if (username == null || username.trim().isEmpty() ||
                emailAddress == null || emailAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("User details cannot not be empty or null");
        }
    }


}
