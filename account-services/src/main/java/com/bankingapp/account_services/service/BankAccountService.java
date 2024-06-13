package com.bankingapp.account_services.service;

import com.bankingapp.account_services.dto.BankResponseDto;
import com.bankingapp.account_services.dto.CreateBankAccountRequestDto;

public interface BankAccountService {

    BankResponseDto createBankAccount(CreateBankAccountRequestDto requestDto);
}
