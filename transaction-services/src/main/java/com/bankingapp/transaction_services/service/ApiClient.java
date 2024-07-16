package com.bankingapp.transaction_services.service;

import com.bankingapp.transaction_services.dto.AmountTransferRequestDto;
import com.bankingapp.transaction_services.dto.UserSecurityInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ACCOUNT-SERVICE",url = "v1/account-services")
public interface ApiClient {

    @GetMapping("/account-exists/{accountNumber}")
    boolean findAccountExists(@PathVariable(value = "accountNumber") String accountNumber);

    @PostMapping("/debit-money")
    HttpStatus debitFromAnAccount(@RequestBody AmountTransferRequestDto requestDto);

    @PostMapping("/credit-money")
    HttpStatus creditToAnAccount(@RequestBody AmountTransferRequestDto requestDto);

    @GetMapping("/get-user")
    UserSecurityInfo getUser(@RequestParam(value = "email")String email);

}
