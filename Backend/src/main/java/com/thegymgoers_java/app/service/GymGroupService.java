package com.thegymgoers_java.app.service;

import com.thegymgoers_java.app.model.GymGroup;
import com.thegymgoers_java.app.model.User;
import com.thegymgoers_java.app.model.Workout;
import com.thegymgoers_java.app.payload.request.NewGymGroupRequest;
import com.thegymgoers_java.app.repository.GymGroupRepository;
import com.thegymgoers_java.app.repository.UserRepository;
import com.thegymgoers_java.app.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
        // Validate username
        ValidationUtil.validateString(username);
        ValidationUtil.validateString(newGymGroupRequest.getGroupName());

        var userOptional = userRepository.findByUsername(username);

        if(userOptional.isEmpty()){
            throw new Exception("User not found");
        }

        var existingGymGroup = gymGroupRepository.findByGroupName(newGymGroupRequest.getGroupName());

        // Validate group name
        if (newGymGroupRequest.getGroupName() == null || newGymGroupRequest.getGroupName().trim().isEmpty()) {
            throw new IllegalArgumentException("GymGroup must have a name");
        }

        // Check if a GymGroup with the same name already exists
        if (existingGymGroup.isPresent()) {
            throw new Exception("GymGroup with that name exists");
        }

        // Create new GymGroup
        GymGroup gymGroup = new GymGroup();
        gymGroup.setGroupName(newGymGroupRequest.getGroupName());
        User admin = userOptional.get();

        // Add user as admin and member of the GymGroup
        gymGroup.addAdmins(admin.getUsername());
        gymGroup.addMember(admin.getUsername());


        // Save and return the new GymGroup
        GymGroup savedGymGroup = gymGroupRepository.save(gymGroup);

        return savedGymGroup;
    }

    public GymGroup joinGymGroup(String username, String groupName) throws Exception {
        GymGroup gymGroup;
        User user;

        // Validate username
        ValidationUtil.validateString(username);
        ValidationUtil.validateString(groupName);

        var userOptional = userRepository.findByUsername(username);
        var gymGroupOptional = gymGroupRepository.findByGroupName(groupName);


        if(userOptional.isEmpty()){
            throw new Exception("User not found");
        }


        // Check if both user and GymGroup exist
        if (gymGroupOptional.isPresent()) {
            gymGroup = gymGroupRepository.findByGroupName(groupName).get();
            user = userRepository.findByUsername(username).get();
            gymGroup.addMember(user.getUsername());
            return gymGroupRepository.save(gymGroup);
        } else {
            throw new Exception("GymGroup not found");
        }
    }

    public List<GymGroup> getGymGroups(String username) throws Exception {
        ValidationUtil.validateString(username);
        User user;

        var userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            throw new Exception("User not found");
        }

        List<GymGroup> gymGroups = gymGroupRepository.findAllByMembersContains(user.getUsername());

        return gymGroups;

    }

}
