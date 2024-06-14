package com.bankingapp.account_services.service;

import com.bankingapp.account_services.dto.BankResponseDto;
import com.bankingapp.account_services.dto.CreateBankAccountRequestDto;
import org.springframework.web.multipart.MultipartFile;

public interface BankAccountService {

    BankResponseDto createBankAccount(CreateBankAccountRequestDto requestDto
            , MultipartFile file);

    String verifyToken(String token);
}
