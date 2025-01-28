package com.thegymgoers_java.app.service;

import com.thegymgoers_java.app.model.GymGroup;
import com.thegymgoers_java.app.model.User;
import com.thegymgoers_java.app.payload.request.NewGymGroupRequest;
import com.thegymgoers_java.app.repository.GymGroupRepository;
import com.thegymgoers_java.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

//@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class GymGroupServiceTests {

    @Mock
    private GymGroupRepository gymGroupRepository;

    @Mock
    private UserRepository userRepository;

    private GymGroupService gymGroupService;

    @BeforeEach
    void setUp() {
        gymGroupService = new GymGroupService(gymGroupRepository, userRepository);
    }

    @Test
    void testCreateGymGroup_Success() throws Exception {
        NewGymGroupRequest request = new NewGymGroupRequest();
        request.setGroupName("Test Group");

        User user = new User("testuser", "test@email.com", "testpass");
        user.set_Id("1");

        // Mock userRepository to return a valid user
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(gymGroupRepository.findByGroupName(anyString())).thenReturn(Optional.empty());
        when(gymGroupRepository.findByGroupName(anyString())).thenReturn(Optional.empty());
        when(gymGroupRepository.save(any(GymGroup.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GymGroup result = gymGroupService.createGymGroup("testuser", request);

        assertNotNull(result);
        assertEquals("Test Group", result.getGroupName());
        assertEquals(1, result.getAdmins().size());
        assertEquals("testuser", result.getAdmins().get(0));
        assertEquals(1, result.getMembers().size());
        assertEquals("testuser", result.getMembers().get(0));
        verify(gymGroupRepository, times(1)).save(any(GymGroup.class));
    }


    @Test
    void testCreateGymGroup_UsernameNullOrEmpty() {
        NewGymGroupRequest request = new NewGymGroupRequest();
        request.setGroupName("Test Group");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gymGroupService.createGymGroup(null, request);
        });

        assertEquals("Details cannot not be empty or null", exception.getMessage());
    }

    @Test
    void testCreateGymGroup_GroupNameNullOrEmpty() {
        NewGymGroupRequest request = new NewGymGroupRequest();
        request.setGroupName(" ");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gymGroupService.createGymGroup("testuser", request);
        });

        assertEquals("Details cannot not be empty or null", exception.getMessage());
    }

    @Test
    void testCreateGymGroup_GroupNameExists() {
        NewGymGroupRequest request = new NewGymGroupRequest();
        request.setGroupName("Test Group");
        Optional<User> mockUser= Optional.of(new User("mock", "mock", "mock"));

        when(gymGroupRepository.findByGroupName(anyString())).thenReturn(Optional.of(new GymGroup()));
        when(userRepository.findByUsername(anyString())).thenReturn(mockUser);

        Exception exception = assertThrows(Exception.class, () -> {
            gymGroupService.createGymGroup("testuser", request);
        });

        assertEquals("GymGroup with that name exists", exception.getMessage());
    }

    @Test
    void testCreateGymGroup_UserNotFound() {
        NewGymGroupRequest request = new NewGymGroupRequest();
        request.setGroupName("Test Group");

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            gymGroupService.createGymGroup("testuser", request);
        });

        assertEquals("User not found", exception.getMessage());
    }
}
