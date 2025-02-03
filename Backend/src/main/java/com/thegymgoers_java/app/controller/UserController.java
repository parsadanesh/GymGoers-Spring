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

    /**
     * Retrieves the workouts for a specific user.
     *
     * @param username the username of the user
     * @return ResponseEntity with the list of workouts or an error message
     */
    @GetMapping("/users/{username}/workouts")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserWorkouts(@PathVariable String username) {
        try {
            List<Workout> workoutList = userService.getWorkouts(username);
            if (workoutList != null) {
                return new ResponseEntity<>(workoutList, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Error: Workouts not found", HttpStatus.NOT_FOUND);
    }

    /**
     * Adds a workout to a specific user.
     *
     * @param username the username of the user
     * @param workoutToAdd the workout to add
     * @return ResponseEntity with the updated user or an error message
     */
    @PostMapping("/users/{username}/workouts")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> addWorkoutToUser(@PathVariable String username, @Valid @RequestBody Workout workoutToAdd) {
        try {
            User updatedUser = userService.addWorkout(username, workoutToAdd);
            if (updatedUser != null) {
                return new ResponseEntity<>(updatedUser, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Error: Workouts not added", HttpStatus.BAD_REQUEST);
    }


    /**
     * Deletes a workout from a specific user.
     *
     * @param username the username of the user
     * @param _id the id of the workout to delete
     * @return ResponseEntity with the updated user or an error message
     */
    @DeleteMapping("/users/{username}/workouts/{_id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteWorkoutFromUser(@PathVariable String username, @PathVariable String _id) {
        try {
            User updatedUser = userService.deleteWorkout(username, _id);
            if (updatedUser != null) {
                return new ResponseEntity<>(updatedUser, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Error: User not found", HttpStatus.BAD_REQUEST);
    }

    /**
     * Retrieves the total weight in the last 7 days for a specific user.
     *
     * @param username the username of the user
     * @return ResponseEntity with the total workouts or an error message
     */
    @GetMapping("/users/{username}/weeklytotal")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getWeeklyTotalForUser(@PathVariable String username) {
        try {
            int last7Days = userService.getWorkoutsFromLast7Days(username);
            return new ResponseEntity<>(last7Days, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
