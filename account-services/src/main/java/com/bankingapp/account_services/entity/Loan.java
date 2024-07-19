package com.bankingapp.account_services.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal loanAmount;
    private double interestRate;
    private int termInMonths;
    private LocalDateTime applicationTime;
    private String loanStatus;
    private String loanType;
    private String user;
}
