package com.bankingapp.email_services.service.impl;

import com.bankingapp.email_services.dto.EmailRequestDto;
import com.bankingapp.email_services.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;
    @Override
    public void sendEmail(EmailRequestDto requestDto) {

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(senderEmail);
            mailMessage.setTo(requestDto.getRecipient());
            mailMessage.setText(requestDto.getMessage());
            mailMessage.setSubject(requestDto.getSubject());

            mailSender.send(mailMessage);
            System.out.println("mail sent successfully");
        }catch (MailException e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
