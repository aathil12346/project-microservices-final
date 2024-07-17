package com.bankingapp.transaction_services.service.impl;

import com.bankingapp.transaction_services.dto.EmailRequestDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmailWithAttachment(EmailRequestDto requestDto, MultipartFile attachment) throws MessagingException, IOException, MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        // Use the true flag to indicate you need a multipart message
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(requestDto.getRecipient());
        helper.setSubject(requestDto.getSubject());
        helper.setText(requestDto.getMessage());

        // Attach the file to the email
        helper.addAttachment(attachment.getOriginalFilename(), new ByteArrayResource(attachment.getBytes()));

        // Send email
        javaMailSender.send(mimeMessage);
    }
}
