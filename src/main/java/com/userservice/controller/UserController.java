package com.userservice.controller;


import com.userservice.component.OnRegistrationCompleteEvent;
import com.userservice.exception.UserAlreadyExistException;
import com.userservice.model.User;
import com.userservice.model.UserDto;
import com.userservice.model.token.JwtRequest;
import com.userservice.security.JwtTokenUtil;
import com.userservice.security.JwtUserDetailsService;
import com.userservice.service.PasswordManager;
import com.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/users")
@RequiredArgsConstructor

public class UserController {

    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtUserDetailsService userDetailsService;
    private final PasswordManager passwordManager;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> userRegistration(@RequestBody @Valid UserDto userDto, final BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(collectErrors(bindingResult));
        }
        try {
            User userRegistered = userService.registerNewUserAccount(userDto);
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(userRegistered,
                    request.getLocale(), appUrl));
        } catch (UserAlreadyExistException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping("/name")
    public ResponseEntity<?> getUser(Principal user) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserInfo(user.getName()));
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirmRegistration(Principal principal) {
        userService.verifyUser(principal.getName());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {

        UserDetails userDetails;
        try {
            userDetails = userDetailsService
                    .loadUserByUsername(authenticationRequest.getEmail());
            authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    authenticationRequest.getEmail(), authenticationRequest.getPassword()
                            )
                    );

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User account is not verified");
        }


        if (!passwordManager.matchPassword(userDetails.getPassword(), authenticationRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }


        return ResponseEntity.ok()
                .header(
                        HttpHeaders.AUTHORIZATION,
                        jwtTokenUtil.generateToken(userDetails)
                )
                .body(userService.getUserInfo(userDetails.getUsername()));
    }


    private String collectErrors(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(";"));
    }

}
