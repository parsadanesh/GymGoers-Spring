package com.thegymgoers_java.app.service;

import com.thegymgoers_java.app.model.Exercise;
import com.thegymgoers_java.app.model.Workout;
import com.thegymgoers_java.app.util.ValidationUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.thegymgoers_java.app.model.User;
import com.thegymgoers_java.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;



/**
 * Service for managing users.
 *
 * This service provides methods for registering, logging in, and managing user workouts.
 * It handles the business logic and interacts with the repositories to perform
 * CRUD operations on Users and Workouts.
 */
@Service
public class UserService {

    // Password encoder for hashing passwords
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    /**
     * Registers a new user.
     *
     * @param user The user to register.
     * @return The registered user or null if the user already exists.
     */
    public User register(User user) {
        // Throws an exception if the user's email/username used to register is null or
        // empty
        ValidationUtil.validateUserDetails(user.getUsername(), user.getEmailAddress());

        var existingUserByUsername = userRepository.findByUsername(user.getUsername());
        var existingUserByEmail = userRepository.findByEmailAddress(user.getEmailAddress());

        // Returns null if a user already exists with the same email/username
        if (existingUserByUsername.isPresent() || existingUserByEmail.isPresent()) {
            return null;
        }

        // Encodes the password before saving the user to the database
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User addedUser = userRepository.save(user);
        return addedUser;
    }

    /**
     * Logs in a user.
     *
     * @param userLogin The user login details.
     * @return The logged-in user.
     */
    public User login(User userLogin) {
        // Throws an exception if the user's email/username used to register is null or
        // empty
        ValidationUtil.validateUserDetails(userLogin.getUsername(), userLogin.getEmailAddress());

        var existingUserByUsername = userRepository.findByUsername(userLogin.getUsername());
        var existingUserByEmail = userRepository.findByEmailAddress(userLogin.getEmailAddress());
        System.out.println(existingUserByUsername);

        // Checks if a user exists with the same username
        if (existingUserByUsername.isPresent()) {
            User user = existingUserByUsername.get();

            // Checks if the raw password matches the stored encoded password
            if (!(passwordEncoder.matches(userLogin.getPassword(), user.getPassword()))) {
                throw new IllegalArgumentException("Incorrect password: " + userLogin.getPassword());
            }

            return user;
        } else {
            throw new IllegalArgumentException("Incorrect Username: " + userLogin.getUsername());
        }
    }

    /**
     * Retrieves the list of workouts for a user.
     *
     * @param username The username of the user.
     * @return The list of workouts.
     */
    public List<Workout> getWorkouts(String username) throws Exception {
        ValidationUtil.validateString(username);
        User user;
        var userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            throw new NoSuchElementException("User not found");
        }

        return user.getWorkoutsList();
    }

    /**
     * Adds a workout to a user's workout list.
     *
     * @param username     The username of the user.
     * @param workoutToAdd The workout to add.
     * @return The updated user.
     */
    // Retrieves the user by username, throws an exception if the user is not found
    // Sets the current date and time as the creation date of the workout
    // Adds the workout to the user's workout list
    // Saves the updated user to the repository and returns the updated user
    public User addWorkout(String username, Workout workoutToAdd) {
        // Throws an exception if the username is null or empty
        ValidationUtil.validateString(username);

        // Throws error for an invalid Workout
        if (workoutToAdd == null) {
            throw new IllegalArgumentException("Workout to add cannot be null");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        workoutToAdd.setDateCreated(LocalDateTime.now().toString());
        user.addWorkout(workoutToAdd);
        return userRepository.save(user);
    }

    /**
     * Deletes a workout from a user's workout list.
     *
     * @param username The username of the user.
     * @param _id      The ID of the workout to delete.
     * @return The updated user or null if the user is not found.
     */
    public User deleteWorkout(String username, String _id) {
        // Throws an exception if the username is null or empty
        ValidationUtil.validateString(username);
        var userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            for (Workout w : user.getWorkoutsList()) {
                if (w.get_id().equals(_id)) {
                    user.getWorkoutsList().remove(w);
                    System.out.println("workout removed");
                    return userRepository.save(user);
                }
            }
            throw new IllegalArgumentException("Workout not found");
        }
        return null;
    }

    /**
     * Retrieves the total weight lifted in workouts from the last 7 days.
     *
     * @param username The username of the user.
     * @return The total weight lifted.
     */
    public int getWorkoutsFromLast7Days(String username){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        List<Workout> workoutWeek = user.getWorkoutsList().stream()
                .filter(workout -> LocalDateTime.parse(workout.getDateCreated(), formatter).isAfter(sevenDaysAgo))
                .collect(Collectors.toList());

        return calculateTotalWeight(workoutWeek);
    }

    /**
     * Calculates the total weight lifted in a list of workouts.
     *
     * @param workoutList The list of workouts.
     * @return The total weight lifted.
     */
    public int calculateTotalWeight(List<Workout> workoutList){
        int result = 0;
        for(Workout workout: workoutList){
            for(Exercise exercise: workout.getExercises()){
                if(!(exercise.getTime()>0)){
                    int total = (exercise.getSets() * exercise.getReps() * exercise.getWeight());
                    result = result + total;
                }
            }
        }
        return result;
    }
}
