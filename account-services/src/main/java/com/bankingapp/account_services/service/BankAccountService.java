package com.bankingapp.account_services.service;

import com.bankingapp.account_services.dto.*;
import com.bankingapp.account_services.entity.BankAccount;
import com.bankingapp.account_services.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.weaver.ResolvedPointcutDefinition;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    BankResponseDto changeAddress(AddressChangeRequestDto requestDto,MultipartFile governmentId,
                                  HttpServletRequest request);

    boolean doesAccountExists(String accountNumber);

    HttpStatus debitFromAnAccount(AmountTransferRequestDto requestDto);

    HttpStatus creditToAnAccount(AmountTransferRequestDto requestDto);

    UserSecurityInfo getUser(String email);
}
