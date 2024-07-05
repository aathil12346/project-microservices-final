package com.bankingapp.transaction_services.service;

import com.bankingapp.transaction_services.dto.AmountTransferRequestDto;
import com.bankingapp.transaction_services.dto.BankResponseDto;

public interface TransactionService {

    BankResponseDto transfer(AmountTransferRequestDto requestDto);
}
