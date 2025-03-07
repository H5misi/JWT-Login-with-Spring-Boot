package com.example.JWT_Login_with_Spring_Boot.dto;

import lombok.Getter;
import lombok.Setter;


/**
 * DTO (Data Transfer Object) class to verify user's email after registration of password reset
 */
@Getter // Automatically generate getter method for all fields at compile time
@Setter // Automatically generate getter method for all fields at compile time
public class VerifyUserDto {
    private String email;
    private String verificationCode;
}
