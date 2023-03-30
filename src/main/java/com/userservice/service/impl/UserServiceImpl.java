package com.userservice.service.impl;

import com.userservice.exception.UserAlreadyExistException;
import com.userservice.model.Role;
import com.userservice.model.User;
import com.userservice.model.UserDto;
import com.userservice.repository.UserRepository;
import com.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public User registerNewUserAccount(UserDto userDto) {
        if (emailExists(userDto.getEmail())){
            throw new UserAlreadyExistException(String.format("Email %s already is used", userDto.getEmail()));
        }
        return userRepository.save(convertToUser(userDto));
    }


    private User convertToUser(UserDto userDto){
        return User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .role(Role.REGULAR.name())
                .password(passwordEncoder.encode(userDto.getPassword())).build();
    }

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
