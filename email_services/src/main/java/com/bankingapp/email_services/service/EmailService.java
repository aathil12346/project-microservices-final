package com.bankingapp.email_services.service;

import com.bankingapp.email_services.dto.EmailRequestDto;

public interface EmailService {

    void sendEmail(EmailRequestDto requestDto);
}
