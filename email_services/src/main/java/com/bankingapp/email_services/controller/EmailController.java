package com.bankingapp.email_services.controller;

import com.bankingapp.email_services.dto.EmailRequestDto;
import com.bankingapp.email_services.service.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-email")
    public void sendEmail(@RequestBody EmailRequestDto requestDto){

        emailService.sendEmail(requestDto);
    }

}
