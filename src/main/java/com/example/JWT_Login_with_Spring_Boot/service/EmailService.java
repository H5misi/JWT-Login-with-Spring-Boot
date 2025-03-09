package com.example.JWT_Login_with_Spring_Boot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


// Mark this class as Spring-managed component, automatically detect it, and make it available for injecting in other components
@Service
/**
 * This class provides functionality to send HTML-formatted verification email messages
 */
public class EmailService {

    // Automatically inject an instance of JavaMailSender into javaMailSender field when the class object is created
    @Autowired
    private JavaMailSender javaMailSender;


    public void sendVerificationEmail(String to, String subject, String text) throws MessagingException{
        // Create a new MimeMessage which represents an email message
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        // Use MimeMessageHelper to simplify setting up MimeMessage
        // (mimeMessage, true): true -> enable multipart, which allows enhancements (e.g. with attachments)
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);


        mimeMessageHelper.setTo(to); // Set the recipient's email address
        mimeMessageHelper.setSubject(subject); // Set the email's subject
        mimeMessageHelper.setText(text, true); // Set the email's body with HTML content (true -> HTML text)

        javaMailSender.send(mimeMessage); // Send the email
    }
}
