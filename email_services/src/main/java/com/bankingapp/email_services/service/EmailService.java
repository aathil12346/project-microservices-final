package com.bankingapp.email_services.service;

import com.bankingapp.email_services.dto.EmailRequestDto;
import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

public interface EmailService {

    void sendEmail(EmailRequestDto requestDto);
}

