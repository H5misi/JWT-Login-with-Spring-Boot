package com.example.JWT_Login_with_Spring_Boot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.JWT_Login_with_Spring_Boot.dto.LoginUserDto;
import com.example.JWT_Login_with_Spring_Boot.dto.RegisterUserDto;
import com.example.JWT_Login_with_Spring_Boot.dto.VerifyUserDto;
import com.example.JWT_Login_with_Spring_Boot.model.User;
import com.example.JWT_Login_with_Spring_Boot.responses.LoginResponse;
import com.example.JWT_Login_with_Spring_Boot.service.AuthenticationService;
import com.example.JWT_Login_with_Spring_Boot.service.JwtService;


/**
 * Controller class for handling authentication-related request under "/authentication"
 */

@RequestMapping("authentication") // Maps all endpoints in this controller to the base path "authentication"
/**
 * Marks it as REST controller, combines @Controller & @ResponseBody, 
 * meaning all methods return data (e.g. JSON) directly in HTTP response body
 */
@RestController
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    /**
     * - Handles user registration, Mapped to POST("/authentication/signup") 
     * - ResponseEntity: Spring class represents HTTP response, including status code, headers, and body
     * - @RequestBody: tells spring to deserialize the JSON payload request body into the object next to it
     */
    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto){
        User registeredUser = authenticationService.signup(registerUserDto);
        // Return the user details in ResponseEntity with a 200 OK status as JSON
        return ResponseEntity.ok(registeredUser);
    }


    /**
     * Handles user login and authentication
     * Mapped to POST("/authentication/login")
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authentication(@RequestBody LoginUserDto loginUserDto){
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        // Generate a JWT token for authenticated user
        String jwtToken = jwtService.generateToken(authenticatedUser);

        // Create a LoginResponse with the token and expiration time
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }

    /**
     * Handles user verification
     * Mapped to POST("/authentication/verify")
     * <?>: uses a wildcard because the return type varies, a string success message or a string exception message
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto){
        try {
            authenticationService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("Account is verified successfully");
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }


    /**
     * Handles resending verification code
     * Mapped to POST("/authentication/resend")
     * @RequestParam: deserialize individual parameter value from submitted data or URL
     */
    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email){
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent!");
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }
    
}
