package com.bankingapp.transaction_services.service;

import com.bankingapp.transaction_services.dto.AmountTransferRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "http://localhost:8082",value = "ACCOUNT-SERVICE")
public interface ApiClient {

    @GetMapping("/account-exists/{accountNumber}")
    boolean findAccountExists(@PathVariable(value = "accountNumber") String accountNumber);

    @PostMapping("/debit-money")
    ResponseEntity<HttpStatus> debitFromAnAccount(@RequestBody AmountTransferRequestDto requestDto);

    @PostMapping("/credit-money")
    ResponseEntity<HttpStatus> creditToAnAccount(@RequestBody AmountTransferRequestDto requestDto);

}
