package com.bankingapp.account_services.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanDetailsDto {

    private Long id;
    private BigDecimal loanAmount;
    private double preferredInterestRate;
    private int preferredTermInMonths;
    private LocalDateTime applicationTime;
    private String loanStatus;
    private String loanType;
}
