package com.bankingapp.transaction_services.service;

import com.bankingapp.transaction_services.dto.AmountTransferRequestDto;
import com.bankingapp.transaction_services.dto.BankResponseDto;
import com.bankingapp.transaction_services.entity.Transaction;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.io.IOException;
import java.util.List;

public interface TransactionService {

    BankResponseDto transfer(AmountTransferRequestDto requestDto);

    List<Transaction> viewTransactions(String accountNumber);
}


