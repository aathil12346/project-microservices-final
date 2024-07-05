package com.bankingapp.transaction_services.service.impl;

import com.bankingapp.transaction_services.dto.AmountTransferRequestDto;
import com.bankingapp.transaction_services.dto.BankResponseDto;
import com.bankingapp.transaction_services.repository.TransactionRepository;
import com.bankingapp.transaction_services.service.ApiClient;
import com.bankingapp.transaction_services.service.TransactionService;
import com.bankingapp.transaction_services.utils.TransactionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

        ResponseEntity<HttpStatus> debitStatus = apiClient.debitFromAnAccount(requestDto);
        ResponseEntity<HttpStatus> creditStatus = apiClient.creditToAnAccount(requestDto);

        if (debitStatus.getStatusCode().equals(HttpStatus.EXPECTATION_FAILED) || creditStatus.getStatusCode().equals(HttpStatus.EXPECTATION_FAILED)){

            return BankResponseDto.builder()
                    .message(TransactionUtils.TRANSACTION_FAILED_MSG)
                    .statusCode(TransactionUtils.TRANSACTION_FAILED_CODE).build();


        }
        return BankResponseDto.builder()
                .message(TransactionUtils.TRANSACTION_SUCCESS_MSG)
                .statusCode(TransactionUtils.TRANSACTION_SUCCESS_CODE).build();

    }
}
