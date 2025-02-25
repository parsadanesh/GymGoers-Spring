package com.thegymgoers_java.app.controller;

import com.thegymgoers_java.app.configuration.jwt.JwtUtils;
import com.thegymgoers_java.app.configuration.services.UserDetailsImpl;
import com.thegymgoers_java.app.model.ERole;
import com.thegymgoers_java.app.model.User;
import com.thegymgoers_java.app.payload.request.LoginRequest;
import com.thegymgoers_java.app.payload.request.NewUserRequest;
import com.thegymgoers_java.app.payload.response.JwtResponse;
import com.thegymgoers_java.app.payload.response.MessageResponse;
import com.thegymgoers_java.app.repository.UserRepository;
import com.thegymgoers_java.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthController(
                          UserRepository userRepository,
                          PasswordEncoder encoder,
                          AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param loginRequest the login request containing the username and
     * password
     * @return a ResponseEntity containing the JWT token and user details, or an
     * error message if authentication fails
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            // Set the authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // Get roles from user details
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            // Returned JWT response
            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Username not found");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Incorrect password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Unauthorized");
        }
    }

    /**
     * Registers a new user in the system.
     *
     * @param newUserRequest the request containing the new user's details
     * @return a ResponseEntity containing a success message or an error message
     * if registration fails
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody NewUserRequest newUserRequest) {


        // Checking if username is already taken
        if (userRepository.findByUsername(newUserRequest.getUsername()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        // Checking if email is already taken
        if (userRepository.findByEmailAddress(newUserRequest.getEmailAddress()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(newUserRequest.getUsername(),
                newUserRequest.getEmailAddress(),
                encoder.encode(newUserRequest.getPassword()));

        // Set the roles for the new user
        Set<String> strRoles = newUserRequest.getRole();
        Set<ERole> roles = new HashSet<>();

        if (strRoles == null) {
            roles.add(ERole.USER);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        roles.add(ERole.ADMIN);
                        break;
                    default:
                        roles.add(ERole.USER);
                        break;
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
