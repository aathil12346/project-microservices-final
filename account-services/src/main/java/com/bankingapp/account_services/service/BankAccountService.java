package com.bankingapp.account_services.service;

import com.bankingapp.account_services.dto.*;
import com.bankingapp.account_services.entity.BankAccount;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BankAccountService {

    BankResponseDto createBankAccount(CreateBankAccountRequestDto requestDto
            , MultipartFile file);

    String verifyToken(String token);

    String login(LoginInformationDto loginInformationDto);

    UserInfoDto getUserDetails(HttpServletRequest request);

    List<BankAccountInfoDto> getAccountDetails(HttpServletRequest request);

    BankResponseDto addBankAccount(HttpServletRequest request,String accountType);

    BankResponseDto changeAddress(AddressChangeRequestDto requestDto,MultipartFile govermentId,
                                  HttpServletRequest request);
}
