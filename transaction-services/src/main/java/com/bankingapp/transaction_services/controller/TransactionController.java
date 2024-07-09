package com.bankingapp.transaction_services.controller;

import com.bankingapp.transaction_services.dto.AmountTransferRequestDto;
import com.bankingapp.transaction_services.dto.BankResponseDto;
import com.bankingapp.transaction_services.entity.Transaction;
import com.bankingapp.transaction_services.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/transaction-service")
public class TransactionController {
    @Autowired
     private TransactionService transactionService;

    @PostMapping("/transfer")
    public BankResponseDto transfer(@RequestBody AmountTransferRequestDto requestDto){
        return transactionService.transfer(requestDto);
    }

    @PostMapping("/view-transactions")
    public List<Transaction> viewTransactions(@RequestParam("accountNumber")String accountNumber){

        return transactionService.viewTransactions(accountNumber);
    }
}
