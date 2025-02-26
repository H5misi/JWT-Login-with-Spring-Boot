package com.example.JWT_Login_with_Spring_Boot.config;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.example.JWT_Login_with_Spring_Boot.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;




/**
 * - @Components: is Class-level annotation Used to mark the class as Spring-managed bean. 
 *   This means that Spring will "automatically" detect, instantiate, and manage 
 *   the life cycle of the class during component scanning.
 */
@Component
// This class ensures that only authenticated requests are processed (filtering the requests if they authenticated or not)
// OncePerRequestFilter: ensure this filter runs only once per request
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver; // Handles exceptions during authentication
    private final JwtService jwtService; // Extracting and validating JWT token
    private final UserDetailsService userDetailsService; // Load user details from database

    // Constructor
    public JwtAuthenticationFilter(
            HandlerExceptionResolver handlerExceptionResolver,
            JwtService jwtService,
            UserDetailsService userDetailsService) {
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }



    // 🔹 Main filtering logic
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain // Allow passing the request to the next filter
    ) throws ServletException, IOException {
        
        // Retrieve the "Authorization" header from the request
        final String authHeader = request.getHeader("Authorization");

        // Check if the header is missing or does not start with prefix "Bearer"
        // If header / prefix is missing, allow the request to proceed without authentication
        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extracting the JWT token
            final String jwt = authHeader.substring(7); // Remove the prefix "Bearer" (skip the first 7 characters)
            final String userEmail = jwtService.extractUsername(jwt);

            // Retrieve the current authentication status from "SecurityContextHolder"
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (userEmail != null && authentication == null) {
                // Load the user details (email, hashed password, authorities) from the database
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // Check if the JWT token is valid (not expired and matches the correct user)
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Create an authentication object fot the user
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, // username & roles
                            null, // No credentials (password) needed (JWT already validated)
                            userDetails.getAuthorities()); // roles & permissions

                    // Attach request details to authentication object
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Store authentication in SecurityContext (marking the user as authenticated)
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

            // Passing the request forward (continue with the request processing)
            filterChain.doFilter(request, response);

        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }

    
    
}
