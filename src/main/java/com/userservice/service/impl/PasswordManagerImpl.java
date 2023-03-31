package com.userservice.service.impl;


import com.userservice.service.PasswordManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordManagerImpl implements PasswordManager {

    private final PasswordEncoder passwordEncoder;

    public String encodePassword(String password){
        return passwordEncoder.encode(password);
    }

    public boolean matchPassword(String encodedPassword, String password){
        return passwordEncoder.matches(password, encodedPassword);
    }
}
