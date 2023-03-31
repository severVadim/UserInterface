package com.userservice.service.impl;

import com.userservice.exception.UserAlreadyExistException;
import com.userservice.model.Role;
import com.userservice.model.User;
import com.userservice.model.UserDto;
import com.userservice.repository.UserRepository;
import com.userservice.service.PasswordManager;
import com.userservice.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordManager passwordManager;
    @Override
    public User registerNewUserAccount(UserDto userDto) {
        if (emailExists(userDto.getEmail())){
            throw new UserAlreadyExistException(String.format("Email %s already is used", userDto.getEmail()));
        }
        return userRepository.save(convertToAfterRegistration(userDto));
    }


    @Override
    public void saveRegisteredUser(User user) {
        userRepository.save(user);
    }


    private User convertToAfterRegistration(UserDto userDto){
        return User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .role(Role.REGULAR.name())
                .enabled(false)
                .password(passwordManager.encodePassword(userDto.getPassword())).build();
    }

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
