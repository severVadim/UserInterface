package com.userservice.service;

public interface PasswordManager {
    String encodePassword(String password);
    boolean matchPassword(String encodedPassword, String password);
}
