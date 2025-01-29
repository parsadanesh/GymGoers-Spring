package com.thegymgoers_java.app.controller;

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.thegymgoers_java.app.configuration.jwt.JwtUtils;
import com.thegymgoers_java.app.configuration.services.UserDetailsImpl;
import com.thegymgoers_java.app.model.User;
import com.thegymgoers_java.app.payload.request.LoginRequest;
import com.thegymgoers_java.app.payload.request.NewUserRequest;
import com.thegymgoers_java.app.repository.UserRepository;
import com.thegymgoers_java.app.service.UserService;


/**
 * This class contains unit tests for the AuthController.
 * It tests the registration and login functionalities.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTests {

    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private AuthController authController;

    @MockBean
    UserRepository userRepository;

    private Optional<User> mockUser;

    private User user;
    private NewUserRequest newUserRequest;
    private LoginRequest loginRequest;

    /**
     * Sets up the test environment before each test method.
     * This method initializes the necessary objects and configurations for
     * testing.
     */
    @BeforeEach
    void setUp() {
        initializeNewUserRequest();
        initializeLoginRequest();
        initializeUsers();
        initializeMockMvc();
    }

    private void initializeNewUserRequest() {
        newUserRequest = new NewUserRequest();
        newUserRequest.setUsername("testuser");
        newUserRequest.setPassword("pass");
        newUserRequest.setEmailAddress("pass@email.com");
    }

    /**
     * Initializes the login request object with test data.
     * This method sets up a login request with a username and password.
     */
    private void initializeLoginRequest() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("pass");
    }

    /**
     * Initializes the user object with test data.
     * This method sets up a user object with a username, email, and password.
     */
    private void initializeUsers() {
        user = new User("testuser", "pass@email.com", "pass");
    }

    /**
     * Initializes the MockMvc object for testing the AuthController.
     * This method sets up the MockMvc instance with the AuthController.
     */
    private void initializeMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    /**
     * This nested class contains unit tests for the registration functionality
     * of the AuthController.
     * It tests various scenarios such as successful registration, registration
     * with an existing username or email, and invalid registration requests.
     */
    @Nested
    class RegisterTests {

        @Test
        void createUserHttpRequest() throws Exception {
            // Mocking a successful response from the database
            when(userRepository.save(user)).thenReturn(user);

            // Mocking a call to the api with valid registration details
            // Asserting a successful 200 response
            mockMvc.perform(post("/api/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(user)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("User registered successfully!"))
                    .andDo(print());
        }

        @Test
        void createUserWithSameUsername() throws Exception {
            // Mocking a user with the same email/username response
            when(userRepository.findByUsername(newUserRequest.getUsername())).thenReturn(Optional.of(user));

            // Mocking a call to the api with registration with the same username
            // Asserting a unsuccessful 400 response
            mockMvc.perform(post("/api/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Error: Username is already taken!"))
                    .andDo(print());
        }

        @Test
        void createUserWithSameEmail() throws Exception {
            // Mocking a user with the same email/username response
            when(userRepository.findByEmailAddress(newUserRequest.getEmailAddress())).thenReturn(Optional.of(user));

            // Mocking a call to the api with registration with the same username
            // Asserting a unsuccessful 400 response
            mockMvc.perform(post("/api/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Error: Email is already in use!"))
                    .andDo(print());
        }

        @Test
        void createUserWithNullUsername() throws Exception {
            // Mocking a user with the same email/username response
            newUserRequest.setUsername(null);
            // when(userRepository.findByUsername(newUserRequest.getUsername())).thenReturn(Optional.of(user));
            // when(userRepository.save(user)).thenReturn(user);

            // Mocking a call to the api with registration with the same username
            // Asserting a unsuccessful 400 response
            mockMvc.perform(post("/api/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason("Invalid request content."))
                    .andDo(print());
        }

        @Test
        void creatingNullUser() throws Exception {
            User newUser = new User("testuser", null, "fakepass");

            // Asserting a unsuccessful 400 response
            mockMvc.perform(post("/api/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newUser)))
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason("Invalid request content."))
                    .andDo(print());
        }
    }

    /**
     * This nested class contains unit tests for the login functionality
     * of the AuthController.
     * It tests various scenarios such as successful login, login with incorrect
     * credentials, and invalid login requests.
     */
    @Nested
    class LoginTests {

        @Test
        void shouldReturn401IfUsernameDoesNotMatch() throws Exception {
            // Mocking a user with the same email/username response
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUsername("invalidusername");
            loginRequest.setPassword("pass");

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new RuntimeException("Bad credentials"));

            mockMvc.perform(post("/api/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized())
                    .andDo(print());

        }

        @Test
        void shouldReturn200IfUsernamePasswordMatch() throws Exception {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUsername("validusername");
            loginRequest.setPassword("validpassword");

            Authentication authentication = mock(Authentication.class);
            UserDetailsImpl userDetails = mock(UserDetailsImpl.class);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(jwtUtils.generateJwtToken(authentication)).thenReturn("valid-jwt-token");

            mockMvc.perform(post("/api/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andDo(print());
        }

        @Test
        void checkWhenPasswordDontMatch() throws Exception {

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            // Asserting an unsuccessful 401 response
            mockMvc.perform(post("/api/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string("Error: Incorrect password"))
                    .andDo(print());
        }

        @Test
        void shouldReturn400IfUsernameIsEmpty() throws Exception {
            loginRequest.setUsername(null);

            // Asserting a unsuccessful 400 response
            mockMvc.perform(post("/api/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(status().reason("Invalid request content."))
                    .andDo(print());
        }

    }
}
