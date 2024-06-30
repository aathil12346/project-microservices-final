package com.bankingapp.account_services.dto;

import com.bankingapp.account_services.entity.AccountType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankAccountDetailsDto {

    private String accountNumber;
    private BigDecimal accountBalance;
    private AccountType accountType;
    private String accountStatus;
    private double interestRate;
    private double overdraftLimit;
}
