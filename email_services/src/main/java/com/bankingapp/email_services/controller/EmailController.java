package com.bankingapp.email_services.controller;

import com.bankingapp.email_services.dto.EmailRequestDto;
import com.bankingapp.email_services.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-email")
    public void sendEmail(@RequestBody EmailRequestDto requestDto){

        emailService.sendEmail(requestDto);
    }
}
