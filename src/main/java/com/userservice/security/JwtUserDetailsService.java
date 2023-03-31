package com.userservice.security;

import com.userservice.exception.UserIsNotVerifiedException;
import com.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<com.userservice.model.User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.map(user -> new User(user.getEmail(), user.getPassword(), user.isEnabled(),
                true, true,true,
                new ArrayList<>())).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + email));
    }

}