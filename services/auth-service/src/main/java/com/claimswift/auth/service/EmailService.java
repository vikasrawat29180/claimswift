package com.claimswift.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtp(String to, String otp){

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("navyakhairatabad@gmail.com");
        mail.setTo(to);
        mail.setSubject("Your OTP Code");
        mail.setText("Your verification code is: " + otp + "\nValid for 5 minutes.");

        mailSender.send(mail);
    }
}