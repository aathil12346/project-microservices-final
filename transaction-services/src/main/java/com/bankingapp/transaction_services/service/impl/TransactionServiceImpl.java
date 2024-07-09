package com.bankingapp.transaction_services.service.impl;

import com.bankingapp.transaction_services.dto.AmountTransferRequestDto;
import com.bankingapp.transaction_services.dto.BankResponseDto;
import com.bankingapp.transaction_services.entity.Transaction;
import com.bankingapp.transaction_services.repository.TransactionRepository;
import com.bankingapp.transaction_services.service.ApiClient;
import com.bankingapp.transaction_services.service.TransactionService;
import com.bankingapp.transaction_services.utils.TransactionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private ApiClient apiClient;
    @Override
    public BankResponseDto transfer(AmountTransferRequestDto requestDto) {

        boolean doesSenderAccountExists = apiClient.findAccountExists(requestDto.getSenderAccountNumber());

        if (!doesSenderAccountExists){
            return BankResponseDto.builder()
                    .message(TransactionUtils.SENDER_ACCOUNT_DOES_NOT_EXISTS_MSG)
                    .statusCode(TransactionUtils.SENDER_ACCOUNT_DOES_NOT_EXISTS_CODE).build();
        }

        boolean doesReceiverAccountExists = apiClient.findAccountExists(requestDto.getRecipientAccountNumber());
        if (!doesReceiverAccountExists){
            return BankResponseDto.builder()
                    .message(TransactionUtils.RECEIVER_ACCOUNT_DOES_NOT_EXISTS_MSG)
                    .statusCode(TransactionUtils.RECEIVER_ACCOUNT_DOES_NOT_EXISTS_CODE).build();
        }

        HttpStatus debitStatus = apiClient.debitFromAnAccount(requestDto);

        if (debitStatus.equals(HttpStatus.EXPECTATION_FAILED)){
            return BankResponseDto.builder()
                    .message(TransactionUtils.TRANSACTION_FAILED_MSG)
                    .statusCode(TransactionUtils.TRANSACTION_FAILED_CODE).build();
        }
        HttpStatus creditStatus = apiClient.creditToAnAccount(requestDto);

        if (creditStatus.equals(HttpStatus.EXPECTATION_FAILED)){
            return BankResponseDto.builder()
                    .message(TransactionUtils.TRANSACTION_FAILED_MSG)
                    .statusCode(TransactionUtils.TRANSACTION_FAILED_CODE).build();
        }



        Transaction transaction = Transaction.builder()
                .amount(requestDto.getAmountToBeTransferred())
                .senderAccountNumber(requestDto.getSenderAccountNumber())
                .receiverAccountNumber(requestDto.getRecipientAccountNumber())
                .status("PROCESSED")
                .timeOfTransaction(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

        return BankResponseDto.builder()
                .message(TransactionUtils.TRANSACTION_SUCCESS_MSG)
                .statusCode(TransactionUtils.TRANSACTION_SUCCESS_CODE).build();

    }

    @Override
    public List<Transaction> viewTransactions(String accountNumber) {
        return transactionRepository.findTransactionBySenderAccountNumberOrReceiverAccountNumber(accountNumber,accountNumber);
    }
}

