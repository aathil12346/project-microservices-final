package com.bankingapp.account_services.service;

import com.bankingapp.account_services.dto.EmailRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "http://localhost:8080",value = "EMAIL-SERVICE")
public interface ApiClient {

    @PostMapping("send-email")
    void sendEmail(@RequestBody EmailRequestDto requestDto);
}
