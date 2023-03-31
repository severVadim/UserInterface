package com.userservice.configuration;


import com.userservice.model.Role;
import com.userservice.model.User;
import com.userservice.model.UserDto;
import com.userservice.repository.UserRepository;
import com.userservice.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initializeAdminUser() {
        if (userRepository.findByRole(Role.ADMIN.name()).isEmpty()) {
            userRepository.save(User.builder()
                    .role(Role.ADMIN.name())
                    .firstName(Role.ADMIN.name())
                    .lastName(Role.ADMIN.name())
                    .email("dsadasda123231fdf@gmail.com")
                    .enabled(true)
                    .password(passwordEncoder.encode("admin"))
                    .build());
        }
    }

}
