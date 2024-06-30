package com.bankingapp.account_services.dto;

import com.bankingapp.account_services.entity.AccountType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankAccountInfoDto {


    private String accountNumber;
    private BigDecimal accountBalance;
    private AccountType accountType;
    private String accountStatus;
    private double interestRate;
    private double overdraftLimit;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

}
