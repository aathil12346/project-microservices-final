package com.bankingapp.transaction_services.controller;

import com.bankingapp.transaction_services.dto.AmountTransferRequestDto;
import com.bankingapp.transaction_services.dto.BankResponseDto;
import com.bankingapp.transaction_services.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/transaction-service")
public class TransactionController {
    @Autowired
     private TransactionService transactionService;

    @PostMapping("/transfer")
    public BankResponseDto transfer(@RequestBody AmountTransferRequestDto requestDto){
        return transactionService.transfer(requestDto);
    }
}
