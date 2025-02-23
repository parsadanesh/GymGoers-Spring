package com.thegymgoers_java.app.util;

/**
 * Utility class for validation.
 *
 * This class provides static methods for validating strings and user details.
 * It throws exceptions if the validation fails.
 */
public class ValidationUtil {

    /**
     * Validates if the string is null or empty.
     *
     * @param string the string to validate
     * @throws IllegalArgumentException if the string is null or empty
     */
    public static void validateString(String string) {
        if (isNullOrEmpty(string)) {
            throw new IllegalArgumentException("Details cannot be empty or null");
        }
    }

    /**
     * Validates if the string is null or empty based on the request type.
     *
     * @param requestType the class type of the request
     * @param string the string to validate
     * @throws IllegalArgumentException if the string is null or empty
     */
    public static void validateString(Class<?> requestType, String string) {
        if (isNullOrEmpty(string)) {
            switch (requestType.getSimpleName()){
                case "NewGymGroupRequest":
                    throw new IllegalArgumentException("GymGroup must have a name");
            }
        }
    }

    /**
     * Validates if the user's email or username used to register is null or empty.
     *
     * @param username the username to validate
     * @param emailAddress the email address to validate
     * @throws IllegalArgumentException if the username or email address is null or empty
     */
    public static void validateUserDetails(String username, String emailAddress) {
        if (isNullOrEmpty(username) || isNullOrEmpty(emailAddress)) {
            throw new IllegalArgumentException("User details cannot be empty or null");
        }
    }

    /**
     * Helper method to check if a string is null or empty.
     *
     * @param string the string to check
     * @return true if the string is null or empty, false otherwise
     */
    private static boolean isNullOrEmpty(String string) {
        return string == null || string.trim().isEmpty();
    }

}
