package com.userservice.service;

import com.userservice.model.User;
import com.userservice.model.UserDto;
import com.userservice.security.model.UserDetailsImpl;

public interface UserService {

    User registerNewUserAccount(UserDto userDto);
    void saveRegisteredUser(User user);
    UserDetailsImpl getUserInfo(String email);

    void verifyUser(String email);

}
