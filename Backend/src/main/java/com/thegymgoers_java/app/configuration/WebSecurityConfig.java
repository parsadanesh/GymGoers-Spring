package com.thegymgoers_java.app.configuration;

import com.thegymgoers_java.app.configuration.jwt.AuthEntryPointJwt;
import com.thegymgoers_java.app.configuration.jwt.AuthTokenFilter;
import com.thegymgoers_java.app.configuration.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * WebSecurityConfig class configures the security settings for the application.
 * It sets up authentication, authorization, CORS, and session management.
 * This class uses JWT for authentication and configures CORS to allow requests from specific origins.
 */
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, AuthEntryPointJwt unauthorizedHandler) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
    }


    /**
     * Creates a bean for the AuthTokenFilter which is responsible for filtering
     * and validating JWT tokens.
     *
     * @return an instance of AuthTokenFilter
     */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }


    /**
     * Creates a bean for the DaoAuthenticationProvider which is responsible for
     * retrieving user details and encoding passwords.
     *
     * @return an instance of DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Creates a bean for the PasswordEncoder which is responsible for encoding
     * passwords.
     *
     * @return an instance of BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * Configures the security filter chain for the application.
     *
     * This method sets up the security configuration, including disabling CSRF protection,
     * configuring CORS, handling exceptions, managing sessions, and setting authorization rules.
     * It also adds a custom JWT authentication filter before the UsernamePasswordAuthenticationFilter.
     *
     * @param http the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs while configuring the security filter chain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Disable CSRF protection as it is not needed for stateless APIs
        http.csrf(csrf -> csrf.disable())
                // Configure CORS settings
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Handle authentication exceptions
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                // Set session management to stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Allow all requests to the /api/auth/** endpoints - login & signup
                        .requestMatchers("/api/auth/**").permitAll()
                        // Require authentication for any other requests
                        .anyRequest().authenticated()
                )
                // Set the authentication provider
                .authenticationProvider(authenticationProvider());

        // Add the JWT authentication filter before the UsernamePasswordAuthenticationFilter
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        // Build and return the SecurityFilterChain
        return http.build();
    }


    /**
     * Creates a bean for the CorsFilter which is responsible for configuring
     * Cross-Origin Resource Sharing (CORS) settings.
     *
     * This method sets up the CORS configuration to allow requests from specific origins,
     * with any headers and methods, and allows credentials to be included in the requests.
     *
     * @return an instance of CorsFilter
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Allow credentials to be included in CORS requests
        config.setAllowCredentials(true);

        // Allow requests from the specified origin
        config.addAllowedOrigin("https://thegymgoerapp.netlify.app");

        // Allow any headers in CORS requests
        config.addAllowedHeader("*");

        // Allow any HTTP methods in CORS requests
        config.addAllowedMethod("*");

        // Register the CORS configuration for all paths
        source.registerCorsConfiguration("/**", config);

        // Return the configured CorsFilter
        return new CorsFilter(source);
    }


    /**
     * Creates a bean for the UrlBasedCorsConfigurationSource which is
     * responsible for
     * configuring CORS settings.
     *
     * @return an instance of UrlBasedCorsConfigurationSource
     */
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("https://thegymgoerapp.netlify.app");
        // for development
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}