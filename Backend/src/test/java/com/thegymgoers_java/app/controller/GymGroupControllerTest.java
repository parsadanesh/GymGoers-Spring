package com.thegymgoers_java.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thegymgoers_java.app.model.GymGroup;
import com.thegymgoers_java.app.model.User;
import com.thegymgoers_java.app.payload.request.NewGymGroupRequest;
import com.thegymgoers_java.app.service.GymGroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GymGroupControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GymGroupService gymGroupService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testname", "testemail@dom.com", "fakepass");
    }

    @Nested
    class CreateGymGroup {

        @Test
        @WithMockUser(username = "testname", roles = { "USER" })
        void shouldReturn201WhenCreatingNewGroup() throws Exception {
            NewGymGroupRequest newGymGroupRequest = new NewGymGroupRequest();
            newGymGroupRequest.setGroupName("testGroupUpdatedtesty");
            newGymGroupRequest.setUsername("testname");

            GymGroup mockGymGroup = new GymGroup();
            mockGymGroup.set_id("67855bf37ecbd73ac508de53");
            mockGymGroup.setGroupName("testGroupUpdatedtety");
            mockGymGroup.addAdmins("677fc378a4b1fc5b9fd40411");
            mockGymGroup.addMember("677fc378a4b1fc5b9fd40411");

            when(gymGroupService.createGymGroup(anyString(), any(NewGymGroupRequest.class)))
                    .thenReturn(mockGymGroup);

            mockMvc.perform(post("/gymgroups/{username}", user.getUsername())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newGymGroupRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(content().json(objectMapper.writeValueAsString(mockGymGroup)))
                    .andDo(print());

        }

        @Test
        @WithMockUser(username = "testname", roles = { "USER" })
        void shouldReturn400WhenCreatingGroupWithInvalidGroupName() throws Exception {
            // Arrange
            NewGymGroupRequest newGymGroupRequest = new NewGymGroupRequest();
            newGymGroupRequest.setGroupName("");
            newGymGroupRequest.setUsername("testname");

            // Mock
            when(gymGroupService.createGymGroup(anyString(), any(NewGymGroupRequest.class)))
                    .thenThrow(new IllegalArgumentException("GymGroup must have a name"));

            // Act & Assert
            mockMvc.perform(post("/gymgroups/{username}", user.getUsername())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newGymGroupRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json("{\"errors\":{\"groupName\":\"GymGroup must have a name\"}}"))
                    .andDo(print());

        }

        @Test
        @WithMockUser(username = "testname", roles = { "USER" })
        void shouldReturn400WhenCreatingGroupWithInvalidUsername() throws Exception {
            // Arrange
            NewGymGroupRequest newGymGroupRequest = new NewGymGroupRequest();
            newGymGroupRequest.setGroupName("validName");
            newGymGroupRequest.setUsername("");

            // Mock
            when(gymGroupService.createGymGroup(anyString(), any(NewGymGroupRequest.class)))
                    .thenThrow(new IllegalArgumentException("GymGroup must have a name"));

            // Act & Assert
            mockMvc.perform(post("/gymgroups/{username}", user.getUsername())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newGymGroupRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json("{\"errors\":{\"username\":\"must not be empty\"}}"))
                    .andDo(print());

        }

        @Test
        @WithMockUser(username = "testname", roles = { "USER" })
        void shouldReturn400WhenCreatingGroupWithNullUsername() throws Exception {
            // Arrange
            NewGymGroupRequest newGymGroupRequest = new NewGymGroupRequest();
            newGymGroupRequest.setGroupName("validName");
            newGymGroupRequest.setUsername(null);

            // Mock
            when(gymGroupService.createGymGroup(anyString(), any(NewGymGroupRequest.class)))
                    .thenThrow(new IllegalArgumentException("User details cannot not be empty or null"));

            // Act & Assert
            mockMvc.perform(post("/gymgroups/{username}", user.getUsername())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newGymGroupRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json("{\"errors\":{\"username\":\"must not be empty\"}}"))
                    .andDo(print());

        }

        @Test
        @WithMockUser(username = "testname", roles = { "USER" })
        void shouldReturn400WhenServiceReturnsNull() throws Exception {
            // Arrange
            NewGymGroupRequest newGymGroupRequest = new NewGymGroupRequest();
            newGymGroupRequest.setGroupName("validName");
            newGymGroupRequest.setUsername("testuser");

            // Mock
            when(gymGroupService.createGymGroup(anyString(), any(NewGymGroupRequest.class)))
                    .thenReturn(null);

            // Act & Assert
            mockMvc.perform(post("/gymgroups/{username}", user.getUsername())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newGymGroupRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Failed to create GymGroup"))
                    .andDo(print());

        }

        @Test
        @WithMockUser(username = "testname", roles = { "USER" })
        void shouldReturn400WhenUserNotFound() throws Exception {
            // Arrange
            NewGymGroupRequest newGymGroupRequest = new NewGymGroupRequest();
            newGymGroupRequest.setGroupName("validname");
            newGymGroupRequest.setUsername("testname");

            // Mock
            when(gymGroupService.createGymGroup(anyString(), any(NewGymGroupRequest.class)))
                    .thenThrow(new Exception("User not found"));

            // Act & Assert
            mockMvc.perform(post("/gymgroups/{username}", user.getUsername())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newGymGroupRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("User not found"))
                    .andDo(print());
        }

        @Test
        @WithMockUser(username = "testname", roles = { "USER" })
        void shouldReturn400ForInvalidJson() throws Exception {
            String invalidJson = "{ \"groupName\": \"testGroup\", \"username\": }"; // Invalid JSON

            mockMvc.perform(post("/gymgroups/{username}", user.getUsername())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        void shouldReturn401ForUnauthorizedAccess() throws Exception {
            NewGymGroupRequest newGymGroupRequest = new NewGymGroupRequest();
            newGymGroupRequest.setGroupName("testGroup");
            newGymGroupRequest.setUsername("testname");

            mockMvc.perform(post("/gymgroups/{username}", user.getUsername())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newGymGroupRequest)))
                    .andExpect(status().isUnauthorized())
                    .andDo(print());
        }

        @Test
        @WithMockUser(username = "testname", roles = { "USER" })
        void shouldReturn400ForNullGroupName() throws Exception {
            NewGymGroupRequest newGymGroupRequest = new NewGymGroupRequest();
            newGymGroupRequest.setGroupName(null);
            newGymGroupRequest.setUsername("testname");

            mockMvc.perform(post("/gymgroups/{username}", user.getUsername())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newGymGroupRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json("{\"errors\":{\"groupName\":\"GymGroup must have a name\"}}"))
                    .andDo(print());
        }
    }

    @Nested
    class AddUserToGymGroup {

        @Test
        @WithMockUser(username = "testname", roles = { "USER" })
        void shouldReturn200WhenAddingUserToGymGroup() throws Exception {
            String username = "testname";
            String groupName = "testGroup";

            GymGroup mockGymGroup = new GymGroup();
            mockGymGroup.setGroupName(groupName);
            mockGymGroup.addMember(username);

            when(gymGroupService.joinGymGroup(anyString(), anyString())).thenReturn(mockGymGroup);

            mockMvc.perform(post("/gymgroups/{username}/{groupName}", username, groupName)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(mockGymGroup)))
                    .andDo(print());
        }


        @Test
        @WithMockUser(username = "testname", roles = { "USER" })
        void shouldReturn400WhenUserNotFound() throws Exception {
            String username = "testname";
            String groupName = "testGroup";

            when(gymGroupService.joinGymGroup(anyString(), anyString())).thenThrow(new Exception("User not found"));

            mockMvc.perform(post("/gymgroups/{username}/{groupName}", username, groupName)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("User not found"))
                    .andDo(print());
        }

        @Test
        @WithMockUser(username = "testname", roles = { "USER" })
        void shouldReturn400WhenGymGroupNotFound() throws Exception {
            String username = "testname";
            String groupName = "testGroup";

            when(gymGroupService.joinGymGroup(anyString(), anyString())).thenThrow(new Exception("GymGroup not found"));

            mockMvc.perform(post("/gymgroups/{username}/{groupName}", username, groupName)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("GymGroup not found"))
                    .andDo(print());
        }

        @Test
        @WithMockUser(username = "testname", roles = { "USER" })
        void shouldReturn400WhenServiceReturnsNull() throws Exception {
            String username = "testname";
            String groupName = "testGroup";

            when(gymGroupService.joinGymGroup(anyString(), anyString())).thenReturn(null);

            mockMvc.perform(post("/gymgroups/{username}/{groupName}", username, groupName)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Failed to add user to GymGroup"))
                    .andDo(print());
        }

        @Test
        @WithMockUser(username = "testname", roles = { "USER" })
        void shouldReturn400ForInvalidGroupName() throws Exception {
            String username = "testname";
            String groupName = "";

            mockMvc.perform(post("/gymgroups/{username}/{groupName}", username, groupName)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(status().reason("No static resource gymgroups/testname."))
                    .andDo(print());
        }
    }

    @Nested
    class GetGymGroups {

        @Test
        @WithMockUser(username = "testname", roles = { "USER" })
        void shouldReturn200WhenGymGroupsFound() throws Exception {
            String username = "testname";
            GymGroup gymGroup = new GymGroup();
            gymGroup.setGroupName("Test Group");

            when(gymGroupService.getGymGroups(anyString())).thenReturn(List.of(gymGroup));

            mockMvc.perform(get("/gymgroups/{username}", username)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(List.of(gymGroup))))
                    .andDo(print());
        }

        @Test
        @WithMockUser(username = "testname", roles = { "USER" })
        void shouldReturn400WhenExceptionThrown() throws Exception {
            String username = "testname";

            when(gymGroupService.getGymGroups(anyString())).thenThrow(new Exception("User not found"));

            mockMvc.perform(get("/gymgroups/{username}", username)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("User not found"))
                    .andDo(print());
        }

        @Test
        @WithMockUser(username = "testname", roles = { "USER" })
        void shouldReturn404WhenNoGymGroupsFound() throws Exception {
            String username = "testname";

            when(gymGroupService.getGymGroups(anyString())).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/gymgroups/{username}", username)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("No GymGroups found"))
                    .andDo(print());
        }

        @Test
        @WithMockUser(username = "testname", roles = { "USER" })
        void shouldReturn400ForInvalidUsername() throws Exception {
            String username = "";

            mockMvc.perform(get("/gymgroups/{username}", username)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Invalid username"))
                    .andDo(print());
        }

        @Test
        @WithMockUser(username = "testname", roles = { "USER" })
        void shouldReturn400ForNullUsername() throws Exception {
            String username = null;

            mockMvc.perform(get("/gymgroups/{username}", username)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(status().reason("No static resource gymgroups."))
                    .andDo(print());
        }
    }
}