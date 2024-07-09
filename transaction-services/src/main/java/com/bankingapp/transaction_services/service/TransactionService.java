package com.bankingapp.transaction_services.service;

import com.bankingapp.transaction_services.dto.AmountTransferRequestDto;
import com.bankingapp.transaction_services.dto.BankResponseDto;
import com.bankingapp.transaction_services.entity.Transaction;

import java.util.List;

public interface TransactionService {

    BankResponseDto transfer(AmountTransferRequestDto requestDto);

    List<Transaction> viewTransactions(String accountNumber);
}
