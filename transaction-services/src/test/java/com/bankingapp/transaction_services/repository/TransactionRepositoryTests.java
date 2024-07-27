package com.bankingapp.transaction_services.repository;

import com.bankingapp.transaction_services.entity.Transaction;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
public class TransactionRepositoryTests {

    @Autowired
    private TransactionRepository transactionRepository;

    private Transaction transaction;


    @BeforeEach
    public void setUp(){

         transaction = Transaction.builder()
                 .timeOfTransaction(LocalDateTime.now())
                 .transactionId(UUID.randomUUID().toString())
                 .status("PROCESSED")
                 .receiverAccountNumber("AATH1234")
                 .senderAccountNumber("AATH3456")
                 .amount(BigDecimal.valueOf(1000L))
                 .build();

    }

    @Test
    @DisplayName("test to save a transaction to database")
    public void saveTransaction(){

        Transaction savedTransaction = transactionRepository.save(transaction);

        Assertions.assertThat(savedTransaction).isNotNull();
    }

    @Test
    @DisplayName("test to retrieve list of transactions from the database")
    public void retrieveTransaction(){

        transactionRepository.save(transaction);

        List<Transaction> transactionList = transactionRepository.findTransactionBySenderAccountNumberOrReceiverAccountNumber("AATH1234","AATH1234");

        Assertions.assertThat(transactionList).isNotNull();
    }
}
