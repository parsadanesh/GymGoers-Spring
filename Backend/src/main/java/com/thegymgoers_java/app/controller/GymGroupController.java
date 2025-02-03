package com.thegymgoers_java.app.controller;

import com.thegymgoers_java.app.model.GymGroup;
import com.thegymgoers_java.app.model.Workout;
import com.thegymgoers_java.app.payload.request.NewGymGroupRequest;
import com.thegymgoers_java.app.service.GymGroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
public class GymGroupController {

    private final GymGroupService gymGroupService;

    @Autowired
    public GymGroupController(GymGroupService gymGroupService){
        this.gymGroupService = gymGroupService;
    }

    @PostMapping("/gymgroups/{username}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> createGymGroup(
            @PathVariable String username,
            @Valid @RequestBody NewGymGroupRequest newGymGroupRequest
    ) {
        try {
            GymGroup gymGroup = gymGroupService.createGymGroup(username, newGymGroupRequest);
            if (gymGroup == null) {
                return new ResponseEntity<>("Failed to create GymGroup", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(gymGroup, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/gymgroups/{username}/{groupName}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> addUserToGymGroup(
            @PathVariable String username,
            @PathVariable String groupName
    ){
        try {
            GymGroup gymGroup = gymGroupService.joinGymGroup(username, groupName);

            if (gymGroup != null) {
                return new ResponseEntity<>(gymGroup, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed to add user to GymGroup", HttpStatus.BAD_REQUEST);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/gymgroups/{username}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getGymGroups(@PathVariable String username){
        return handleGetGymGroups(username);
    }

    private ResponseEntity<?> handleGetGymGroups(String username) {
        try {
            List<GymGroup> gymGroups = gymGroupService.getGymGroups(username);

            if (gymGroups.isEmpty()) {
                return new ResponseEntity<>("No GymGroups found", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(gymGroups, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
