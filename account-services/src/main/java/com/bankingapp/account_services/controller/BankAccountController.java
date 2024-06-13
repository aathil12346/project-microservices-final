package com.bankingapp.account_services.controller;

import com.bankingapp.account_services.dto.BankResponseDto;
import com.bankingapp.account_services.dto.CreateBankAccountRequestDto;
import com.bankingapp.account_services.service.BankAccountService;
import com.bankingapp.account_services.service.S3FileUploadService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/account-services")
public class BankAccountController {

    @Autowired
    private BankAccountService bankAccountService;
    @Autowired
    private S3FileUploadService fileUploadService;

    @PostMapping(value = "/create-account",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BankResponseDto createBankAccount(@Valid @RequestBody CreateBankAccountRequestDto requestDto){

        return bankAccountService.createBankAccount(requestDto);

    }

}
