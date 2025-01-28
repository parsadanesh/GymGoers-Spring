package com.thegymgoers_java.app.controller;

import com.thegymgoers_java.app.model.Workout;
import org.springframework.security.access.prepost.PreAuthorize;

import com.thegymgoers_java.app.model.User;
import com.thegymgoers_java.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{username}/workouts")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getUsersWorkouts(@PathVariable String username) {

        try {
            // Attempting to find list of workouts based on a user's username
            List<Workout> workoutList = userService.getWorkouts(username);

            // Returns the valid list of workouts
            if (workoutList != null) {
                return new ResponseEntity<>(workoutList, HttpStatus.OK);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        }

        return new ResponseEntity<>("Error: Workouts not found", HttpStatus.BAD_REQUEST);

    }

    @PostMapping("/users/{username}/workouts")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> addWorkoutToUser(@PathVariable String username, @Valid @RequestBody Workout workoutToAdd) {

        // Attempting to find list of workouts based on a user's username
        User updatedUser = userService.addWorkout(username, workoutToAdd);

        // Returns the valid list of workouts
        if (updatedUser != null) {
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }
        return new ResponseEntity<>("Error: Workouts not added", HttpStatus.BAD_REQUEST);

    }

    @DeleteMapping("/users/{username}/workouts/{_id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteWorkoutFromUser(@PathVariable String username, @PathVariable String _id) {
        User updatedUser = userService.deleteWorkout(username, _id);

        // Returns the valid list of workouts
        if (updatedUser != null) {
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }

        return new ResponseEntity<>("Error: Workout not deleted", HttpStatus.BAD_REQUEST);

    }

}
