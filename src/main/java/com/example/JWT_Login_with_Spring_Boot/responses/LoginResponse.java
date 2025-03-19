package com.example.JWT_Login_with_Spring_Boot.responses;

import lombok.Getter;
import lombok.Setter;

/**
 * simple container to represent the data sent back (response) to a client after a successful login
 */
@Getter
@Setter
public class LoginResponse {
    
    private String token;
    private Long expiresIn; // indicates the duration until the token expires, typically in seconds or milliseconds


    public LoginResponse(String token, Long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }

    
}
