package com.userservice.controller;


import com.userservice.component.OnRegistrationCompleteEvent;
import com.userservice.service.PasswordManager;
import com.userservice.service.impl.PasswordManagerImpl;
import com.userservice.component.token.JwtTokenUtil;
import com.userservice.component.token.JwtUserDetailsService;
import com.userservice.exception.UserAlreadyExistException;
import com.userservice.model.token.JwtRequest;
import com.userservice.model.User;
import com.userservice.model.UserDto;
import com.userservice.model.token.JwtResponse;
import com.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<?> getUser() {
        return ResponseEntity.status(HttpStatus.OK).body("dsadsadas ");
    }

//    @GetMapping("/regitrationConfirm")
//    public ResponseEntity<?> confirmRegistration(@RequestParam("token") String token) {
//
//        VerificationToken verificationToken = userService.getVerificationToken(token);
//        if (verificationToken == null) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        }
//
//        User user = verificationToken.getUser();
//        Calendar cal = Calendar.getInstance();
//        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        }
//
//        user.setEnabled(true);
//        userService.saveRegisteredUser(user);
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }

    @PostMapping( "/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {

        UserDetails userDetails;
        try {
             userDetails = userDetailsService
                    .loadUserByUsername(authenticationRequest.getEmail());
        } catch (UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        if (!passwordManager.matchPassword(userDetails.getPassword(), authenticationRequest.getPassword())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password is incorrect");
        }

        String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }



    private String collectErrors(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(";"));
    }

}
