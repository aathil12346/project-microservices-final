package com.bankingapp.transaction_services.service;

import com.bankingapp.transaction_services.dto.AmountTransferRequestDto;
import com.bankingapp.transaction_services.dto.UserSecurityInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ACCOUNT-SERVICE")
public interface ApiClient {

    @GetMapping("v1/account-services/account-exists/{accountNumber}")
    boolean findAccountExists(@PathVariable(value = "accountNumber") String accountNumber);

    @PostMapping("v1/account-services/debit-money")
    HttpStatus debitFromAnAccount(@RequestBody AmountTransferRequestDto requestDto);

    @PostMapping("v1/account-services/credit-money")
    HttpStatus creditToAnAccount(@RequestBody AmountTransferRequestDto requestDto);

    @GetMapping("v1/account-services/get-user")
    UserSecurityInfo getUser(@RequestParam(value = "email")String email);

}
