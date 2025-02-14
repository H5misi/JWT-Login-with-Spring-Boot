package com.example.JWT_Login_with_Spring_Boot.model;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "users")
@Getter
@Setter
/*
 * "implements UserDetails" Implements core Spring Security interface for user
 * authentication/roles
 * "UserDetails" interface Provides core user information
 */
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(name = "verification_code")
    private String verificationCode;
    @Column(name = "verification_expiration")
    private LocalDateTime verificationCodeExpireAt;

    private boolean enabled;

    
    // Default constructor
    public User() {
    }

    /**
     * Constructor for creating an unverified new User with basic credentials.
     * User will require authentication before accessing protected resources.
     */
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // This method defines the roles or permissions (also called "authorities") that a user has
    @Override
    /**
     * "Collection" : method will return a group(Collection) of objects
     * "<T>" : return type for the method
     * "?" : return type is (any type)
     * "extends GrantedAuthority" : This is a upper bound on the wildcard. It means
     * the collection can hold objects of any type as long as that type is a
     * subclass of GrantedAuthority (or GrantedAuthority itself).
     */
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override // Checks if account has not expired (always true in this implementation)
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override // Checks if account is not locked (always true in this implementation)
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override // Checks if password has not expired (always true in this implementation)
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override // Controls whether user can authenticate based on enabled status
    public boolean isEnabled(){
        return enabled;
    }
}

