package com.bankingapp.account_services.service;

import com.bankingapp.account_services.dto.BankResponseDto;
import com.bankingapp.account_services.dto.CreateBankAccountRequestDto;
import com.bankingapp.account_services.dto.LoginInformationDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

public interface BankAccountService {

    BankResponseDto createBankAccount(CreateBankAccountRequestDto requestDto
            , MultipartFile file);

    String verifyToken(String token);

    String login(LoginInformationDto loginInformationDto);


}
