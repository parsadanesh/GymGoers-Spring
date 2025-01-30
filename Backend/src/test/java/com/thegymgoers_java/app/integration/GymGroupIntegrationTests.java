package com.thegymgoers_java.app.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thegymgoers_java.app.model.GymGroup;
import com.thegymgoers_java.app.model.User;
import com.thegymgoers_java.app.payload.request.LoginRequest;
import com.thegymgoers_java.app.payload.request.NewGymGroupRequest;
import com.thegymgoers_java.app.repository.GymGroupRepository;
import com.thegymgoers_java.app.repository.UserRepository;
import com.thegymgoers_java.app.service.GymGroupService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Set;

import static com.thegymgoers_java.app.model.ERole.USER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class GymGroupIntegrationTests {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GymGroupRepository gymGroupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GymGroupService gymGroupService;

    private String token;

    @Autowired
    private Environment environment;

    @BeforeEach
    void setUp() throws Exception {
        System.out.println("active: " + Arrays.toString(environment.getActiveProfiles()));

        gymGroupRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User("testuser", "testuser@example.com", passwordEncoder.encode("password"));
        user.setRoles(Set.of(USER));
        userRepository.save(user);

        // Log in the user and get the JWT token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");


        MvcResult result = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println(response);
        token = "Bearer " + objectMapper.readTree(response).get("accessToken").asText();
    }

    @Nested
    class CreateGymGroup {

        @Test
        void shouldReturn201WhenCreatingGymGroup() throws Exception {

            String username = "testuser";
            NewGymGroupRequest request = new NewGymGroupRequest();
            request.setGroupName("Test Group");
            request.setUsername(username);

            mockMvc.perform(post("/gymgroups/{username}", username)
                            .header("Authorization", token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().json("{\"groupName\":\"Test Group\",\"admins\":[\"testuser\"],\"members\":[\"testuser\"]}"))
                    .andDo(print());
        }

        @Test
        void shouldReturn400WhenUserNotFound() throws Exception {
            String username = "nonexistentuser";
            NewGymGroupRequest request = new NewGymGroupRequest();
            request.setGroupName("Test Group");
            request.setUsername(username);

            mockMvc.perform(post("/gymgroups/{username}", username)
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("User not found"))
                    .andDo(print());
        }

        @Test
        void shouldReturn400WhenGymGroupNameExists() throws Exception {
            String username = "testuser";
            NewGymGroupRequest request = new NewGymGroupRequest();
            request.setGroupName("Test Group");
            request.setUsername(username);

            // Create a gym group in the repository
            GymGroup gymGroup = new GymGroup();
            gymGroup.setGroupName("Test Group");
            gymGroup.addAdmins(username);
            gymGroup.addMember(username);
            gymGroupRepository.save(gymGroup);

            mockMvc.perform(post("/gymgroups/{username}", username)
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("GymGroup with that name exists"))
                    .andDo(print());
        }
    }

    @Nested
    class AddUserToGymGroup {

        @Test
        void shouldReturn200WhenAddingUserToGymGroup() throws Exception {
            // Test implementation
        }
    }

    @Nested
    class GetGymGroups {

        @Test
        void shouldReturn200WhenGymGroupsFound() throws Exception {
            // Test implementation
        }
    }
}
