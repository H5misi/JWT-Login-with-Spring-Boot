package com.example.JWT_Login_with_Spring_Boot.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.JWT_Login_with_Spring_Boot.dto.LoginUserDto;
import com.example.JWT_Login_with_Spring_Boot.dto.RegisterUserDto;
import com.example.JWT_Login_with_Spring_Boot.dto.VerifyUserDto;
import com.example.JWT_Login_with_Spring_Boot.model.User;
import com.example.JWT_Login_with_Spring_Boot.repository.UserRepository;

import jakarta.mail.MessagingException;



/**
 * Service class responsible for handling user authentication, registration, and verification processes
 */
@Service
public class AuthenticationService {

    private final UserRepository userRepository; // For interacting with database
    private final PasswordEncoder passwordEncoder; // For securely hashing passwords\
    private final AuthenticationManager authenticationManager; // For authenticating users with Spring Security
    private final EmailService emailService; // Custom service for sending emails
    
    public AuthenticationService(
            UserRepository userRepository, 
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, 
            EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }
    

    /**
     * Register a new user, generates a verification code, sends a verification email, 
     * and save the user to the database
     */
    public User signup(RegisterUserDto registerUserDto){
        User user = new User(
                registerUserDto.getUsername(),
                registerUserDto.getEmail(), 
                passwordEncoder.encode(registerUserDto.getPassword()));

        
        user.setVerificationCode(generateVerificationCode());

        // Set the verification code to expire in 15 minutes
        user.setVerificationCodeExpireAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false); // Disable the account until verification is done
        sendVerificationEmail(user);

        return userRepository.save(user);
    }


    // Authenticate users by checking the credentials and account status
    public User authenticate(LoginUserDto loginUserDto){

        // retrieve the user from db if exist (search using userRepository), or else throw an exception
        User user = userRepository.findByEmail(loginUserDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the user is enabled or not
        if (!user.isEnabled()) {
            throw new RuntimeException("Account is not verified, please verify your account");
        }

        // Check the user's credentials (authenticate) using authenticationManager
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserDto.getEmail(), loginUserDto.getPassword()));

        return user;
    }


    // Verify user's account using verification code
    public void verifyUser(VerifyUserDto verifyUserDto){
        // User Optional<T> to retrieve the user if exist, and return empty if not
        Optional<User> optionalUser = userRepository.findByEmail(verifyUserDto.getEmail());

        // Check if the user is present or not
        if (optionalUser.isPresent()) {
            User user = optionalUser.get(); // use .get() with Optional<T> object to return the value

            // Check if verification code is expired or not
            if (user.getVerificationCodeExpireAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification code is expired!");
            }

            // Verify the provided (user.getVerificationCode) code against the stored code (verifyUserDto.getVerificationCode)
            if (user.getVerificationCode().equals(verifyUserDto.getVerificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpireAt(null);
                userRepository.save(user);
            } else {
                throw new RuntimeException("Invalid verification Code");
            }
            
        } else {
            throw new RuntimeException("User not found");
        }
    }


    // Resend a new verification code if the account is not verified yet
    public void resendVerificationCode(String email){

        Optional<User> optionalUser = userRepository.findByEmail(email);

        // Check if the user is present or not
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Check if the user account is enabled or not, no need to resend if enabled
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            // set a new verification code (randomly generated)
            user.setVerificationCode(generateVerificationCode());
            
            // Set the new verification code expiration time after 1 hour
            user.setVerificationCodeExpireAt(LocalDateTime.now().plusHours(1));

            sendVerificationEmail(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }


    // Send a verification email to the user with the verification code
    private void sendVerificationEmail(User user){

        String subject = "Account verification";
        String verificationCode = "Verification code " + user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException messagingException) {
            // This outputs the exception type, message, and stack trace to help diagnose why the email failed to send
            // TODO: Replace with proper loggig (e.g., SLF4J) in production environments
            messagingException.printStackTrace();
        }
    }


    // Generate a random 6-digit verification code
    private String generateVerificationCode(){
        Random random = new Random();

        /**
         * generate a random 6-digits within range (0 - 899,999), then add 100000 to
         * ensure each time the number will be 6-digits always, so the new range will be (100,000 - 999,999)
         */
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
