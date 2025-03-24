package com.example.JWT_Login_with_Spring_Boot.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;




/**
 * Configuration class for Spring Security, setting up authentication, authorization, session management, and CORS.
 */

@Configuration
@EnableWebSecurity // enable Spring Security features (to secure HTTP requests and handle authentication/authorization)
public class SecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(
            AuthenticationProvider authenticationProvider,
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }


    /**
     * Defines the security filter chain that handles HTTP security configurations.
     */
    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception{
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF (Cross-Site Request Forgery) protection since using JWTs & app is stateless
            // Authorization rules
            .authorizeHttpRequests(
                    authorize -> authorize
                            .requestMatchers("/authentication/**").permitAll() // Allow unauthenticated access to "/auth/**" endpoints
                            .anyRequest().authenticated()) // Require authentication for all other endpoints
            // Use stateless session(no server-side session)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider) // Custom AuthenticationProvider (injected via constructor)
            // Adds JWT filter before the default filter, so JWT tokens are checked first
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
    }


    /**
     * Configure CORS (Cross-Origin Resource Sharing) to control allowed
     * requests (from specific origins (specific domains)), methods, and headers
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Set allowed origins (domains that can access this API)
        corsConfiguration.setAllowedOrigins(List.of(
                "https://app-backend.com", // Production domain
                "http://localhost:8080")); // Local development environment


        // Set allowed HTTP request methods
        corsConfiguration.setAllowedMethods(List.of("POST", "GET", "PUT", "DELETE"));

        // Set allowed headers in the request
        corsConfiguration.setAllowedHeaders(List.of(
                "Authorization", // To include JWT tokens in the header
                "Content-Type")); // To specify the media type of the request body


        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();

        // Register CORS configuration for all endpoints ("/**" -> all endpoints)
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return urlBasedCorsConfigurationSource;
    }
    

}
