package com.bankingapp.account_services.controller;

import com.bankingapp.account_services.dto.BankResponseDto;
import com.bankingapp.account_services.dto.CreateBankAccountRequestDto;
import com.bankingapp.account_services.service.BankAccountService;
import com.bankingapp.account_services.service.S3FileUploadService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("v1/account-services")
public class BankAccountController {

    @Autowired
    private BankAccountService bankAccountService;


    @PostMapping(value = "/create-account",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BankResponseDto createBankAccount(@Valid @RequestPart(value = "requestDto") CreateBankAccountRequestDto requestDto,
                                             @RequestPart(value = "file") MultipartFile governmentId) throws IOException {

        return bankAccountService.createBankAccount(requestDto,governmentId);
    }


}
