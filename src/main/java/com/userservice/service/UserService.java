package com.userservice.service;

import com.userservice.model.User;
import com.userservice.model.UserDto;

public interface UserService {

    User registerNewUserAccount(UserDto userDto);


}
