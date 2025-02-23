package com.thegymgoers_java.app.controller;

import com.thegymgoers_java.app.model.GymGroup;
import com.thegymgoers_java.app.payload.request.NewGymGroupRequest;
import com.thegymgoers_java.app.service.GymGroupService;
import com.thegymgoers_java.app.util.ValidationUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@Validated
public class GymGroupController {

    private final GymGroupService gymGroupService;

    @Autowired
    public GymGroupController(GymGroupService gymGroupService) {
        this.gymGroupService = gymGroupService;
    }

    /**
     * Creates a new GymGroup for the specified user.
     *
     * @param username           the username of the user creating the GymGroup
     * @param newGymGroupRequest the request payload containing the details of the
     *                           new GymGroup
     * @return a ResponseEntity containing the created GymGroup or an error message
     */
    @PostMapping("/gymgroups/{username}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> createGymGroup(
            @PathVariable String username,
            @Valid @RequestBody NewGymGroupRequest newGymGroupRequest) {
        GymGroup gymGroup;

        try {
            gymGroup = gymGroupService.createGymGroup(username, newGymGroupRequest);
            if (gymGroup == null) {
                return new ResponseEntity<>("Failed to create GymGroup", HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(gymGroup, HttpStatus.CREATED);
    }

    @PostMapping("/gymgroups/{username}/{groupName}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> addUserToGymGroup(
            @PathVariable String username,
            @PathVariable String groupName) {
        GymGroup gymGroup;
        try {
            gymGroup = gymGroupService.joinGymGroup(username, groupName);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        if (gymGroup == null) {
            return new ResponseEntity<>("Failed to add user to GymGroup", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(gymGroup, HttpStatus.OK);
    }

    /**
     * Retrieves the GymGroups for the specified user.
     *
     * @param username the username of the user whose GymGroups are to be retrieved
     * @return a ResponseEntity containing the list of GymGroups or an error message
     */
    @GetMapping("/gymgroups/{username}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getGymGroups(@PathVariable String username) {
        try {
            ValidationUtil.validateString(username);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return handleGetGymGroups(username);
    }

    /**
     * Handles the retrieval of GymGroups for the specified user.
     *
     * @param username the username of the user whose GymGroups are to be retrieved
     * @return a ResponseEntity containing the list of GymGroups or an error message
     */
    private ResponseEntity<?> handleGetGymGroups(String username) {
        List<GymGroup> gymGroups;
        try {
            gymGroups = gymGroupService.getGymGroups(username);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        if (gymGroups.isEmpty()) {
            return new ResponseEntity<>("No GymGroups found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(gymGroups, HttpStatus.OK);
    }

}
