package com.bankingapp.transaction_services.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transactions")
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String transactionId;
    private String senderAccountNumber;
    private BigDecimal amount;
    private String receiverAccountNumber;
    private String status;
    private LocalDateTime timeOfTransaction;

}