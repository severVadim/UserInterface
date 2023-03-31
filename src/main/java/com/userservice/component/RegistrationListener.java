package com.userservice.component;

import com.userservice.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import com.userservice.model.User;


@Component
@RequiredArgsConstructor
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final JavaMailSender mailSender;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();

        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(recipientAddress);
        email.setTo(recipientAddress);
        email.setSubject(subject);
        String link = String.format("http://localhost:3000/confirm?token=%s", jwtTokenUtil.generateToken(user.getEmail()));
        email.setText("\r\n" + String.format("Hello %s, click on link from below to activate your account ", user.getFirstName()) +
                "\r\n" + link);
        mailSender.send(email);
    }
}