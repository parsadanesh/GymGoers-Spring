package com.thegymgoers_java.app.service;

import com.thegymgoers_java.app.model.GymGroup;
import com.thegymgoers_java.app.model.User;
import com.thegymgoers_java.app.model.Workout;
import com.thegymgoers_java.app.payload.request.NewGymGroupRequest;
import com.thegymgoers_java.app.repository.GymGroupRepository;
import com.thegymgoers_java.app.repository.UserRepository;
import com.thegymgoers_java.app.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class GymGroupService {

    private final GymGroupRepository gymGroupRepository;
    private final UserRepository userRepository;

    @Autowired
    public GymGroupService(GymGroupRepository gymGroupRepository, UserRepository userRepository) {
        this.gymGroupRepository = gymGroupRepository;
        this.userRepository = userRepository;
    }

    public GymGroup createGymGroup(String username, NewGymGroupRequest newGymGroupRequest) throws Exception {
        validateInputs(username, newGymGroupRequest);
        User admin = getUserByUsername(username);
        checkIfGymGroupExists(newGymGroupRequest.getGroupName());
        GymGroup gymGroup = createNewGymGroup(newGymGroupRequest.getGroupName(), admin);
        return gymGroupRepository.save(gymGroup);
    }

    private void validateInputs(String username, NewGymGroupRequest newGymGroupRequest) throws IllegalArgumentException {
        ValidationUtil.validateString(username);
        ValidationUtil.validateString(NewGymGroupRequest.class, newGymGroupRequest.getGroupName());
    }

    private User getUserByUsername(String username) {
        var userOptional = userRepository.findByUsername(username);
        return userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private void checkIfGymGroupExists(String groupName) {
        var existingGymGroup = gymGroupRepository.findByGroupName(groupName);
        if (existingGymGroup.isPresent()) {
            throw new IllegalArgumentException("GymGroup with that name exists");
        }
    }

    /**
     * Creates a new GymGroup with the specified group name and admin.
     *
     * @param groupName the name of the new gym group
     * @param admin the user who will be the admin of the new gym group
     * @return the created GymGroup
     */
    private GymGroup createNewGymGroup(String groupName, User admin) {
        // Create a new GymGroup instance
        GymGroup gymGroup = new GymGroup();
        
        // Set the group name
        gymGroup.setGroupName(groupName);
        
        // Add the admin to the list of admins
        gymGroup.addAdmins(admin.getUsername());
        
        // Add the admin to the list of members
        gymGroup.addMember(admin.getUsername());
        
        // Return the created GymGroup
        return gymGroup;
    }

    /**
     * Allows a user to join an existing GymGroup.
     *
     * @param username the username of the user
     * @param groupName the name of the GymGroup
     * @return the updated GymGroup
     * @throws Exception if any validation or lookup fails
     */
    public GymGroup joinGymGroup(String username, String groupName) throws Exception {
        // Validate the input parameters
        validateInputs(username, groupName);
        
        // Retrieve the user by username
        User user = getUserByUsername(username);
        
        // Retrieve the GymGroup by group name
        GymGroup gymGroup = getGymGroupByName(groupName);
        
        // Add the user to the GymGroup
        addUserToGymGroup(user, gymGroup);
        
        // Save and return the updated GymGroup
        return gymGroupRepository.save(gymGroup);
    }

    private void validateInputs(String username, String groupName) {
        ValidationUtil.validateString(username);
        ValidationUtil.validateString(groupName);
    }

    private GymGroup getGymGroupByName(String groupName) {
        var gymGroupOptional = gymGroupRepository.findByGroupName(groupName);
        return gymGroupOptional.orElseThrow(() -> new NoSuchElementException("GymGroup not found"));
    }

    private void addUserToGymGroup(User user, GymGroup gymGroup) {
        if (!gymGroup.getMembers().contains(user.getUsername())) {
            gymGroup.addMember(user.getUsername());
        }
    }

    /**
     * Retrieves the list of GymGroups that the specified user is a member of.
     *
     * @param username the username of the user
     * @return a list of GymGroups that the user is a member of
     * @throws Exception if the user is not found or if validation fails
     * */
    
    public List<GymGroup> getGymGroups(String username) throws Exception {
        ValidationUtil.validateString(username);
        Optional<User> userOptional = userRepository.findByUsername(username);
        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return gymGroupRepository.findAllByMembersContains(user.getUsername());


    }

}
