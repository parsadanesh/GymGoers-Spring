package com.thegymgoers_java.app.util;

public class ValidationUtil {

    // Throws an exception if the String is null or empty
    public static void validateString(String string) {
        if (isNullOrEmpty(string)) {
            throw new IllegalArgumentException("Details cannot be empty or null");
        }
    }

    // Throws an exception if the String is null or empty
    public static void validateString(Class<?> requestType, String string) {
        if (isNullOrEmpty(string)) {
            switch (requestType.getSimpleName()){
                case "NewGymGroupRequest":
                    throw new IllegalArgumentException("GymGroup must have a name");
//                default:
//                    throw new IllegalArgumentException("Details cannot be empty or null");
            }
        }
    }

    // Throws an exception if the user's email/username used to register is null or
    // empty
    public static void validateUserDetails(String username, String emailAddress) {
        if (isNullOrEmpty(username) || isNullOrEmpty(emailAddress)) {
            throw new IllegalArgumentException("User details cannot be empty or null");
        }
    }

    // Helper method to check if a string is null or empty
    private static boolean isNullOrEmpty(String string) {
        return string == null || string.trim().isEmpty();
    }

}
