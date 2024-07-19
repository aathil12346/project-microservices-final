package com.bankingapp.account_services.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanRequestDto {


    @Min(value = 500,message = "Borrowing limit starts from 500")
    private BigDecimal loanAmount;
    @Min(value = 6,message = "interest rate starts from 6")
    @Max(value = 36,message = "interest rate starts from 36")
    private double preferredInterestRate;
    @Min(value = 24,message = "minimum terms start from 12 months")
    private int preferredTermsInMonths;
}
