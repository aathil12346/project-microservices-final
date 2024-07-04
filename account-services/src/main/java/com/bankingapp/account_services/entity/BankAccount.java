package com.bankingapp.account_services.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_accounts")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankAccount {

    @Id
    @SequenceGenerator(name = "account_id_sequence",sequenceName =  "account_id_sequence",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator =  "account_id_sequence")
    private Long id;
    private String accountNumber;
    private BigDecimal accountBalance;
    @Enumerated
    private AccountType accountType;
    private String accountStatus;
    private double interestRate;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime modifiedAt;
    @ManyToOne(optional = false)
    @JoinColumn(referencedColumnName = "id",name = "user_id")
    private User user;
}
