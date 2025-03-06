package com.example.JWT_Login_with_Spring_Boot.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;


/**
 * Configuration class for setting up email functionality 
 */
@Configuration
public class EmailConfiguration {

    // Injecting email username and password from application.properties file
    @Value("${spring.mail.username}")
    private String emailUsername;
    @Value("${spring.mail.password}")
    private String emailPassword;


    @Bean
    public JavaMailSender javaMailSender(){
        
        // Configure email server details & authentication credentials
        JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
        javaMailSenderImpl.setHost("smtp.gmail.com"); // Set the Host to Gmail SMTP server
        javaMailSenderImpl.setPort(587); // Set the Port to standard port for SMTP with TLS encryption
        javaMailSenderImpl.setUsername(emailUsername);
        javaMailSenderImpl.setPassword(emailPassword);


        // Configure additional mail properties
        Properties properties = javaMailSenderImpl.getJavaMailProperties();
        properties.put("mail.transport.protocol", "smtp"); // Set the transport protocol to SMTP
        properties.put("mail.smtp.auth", "true"); // Enable SMTP authentication (require username & password)
        // Enable STARTTLS to upgrade insecure connection to secure connection with TLS encryption
        properties.put("mail.smtp.starttls.enable", "true");
        // Enable debug mode to log detailed info about email-sending process
        properties.put("mail.debug", "true");

        return javaMailSenderImpl;
    }
}
