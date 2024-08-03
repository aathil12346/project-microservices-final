package com.bankingapp.transaction_services.repository;

import com.bankingapp.transaction_services.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,String> {

    List<Transaction> findTransactionBySenderAccountNumberOrReceiverAccountNumber(String senderAccountNumber, String receiverAccountNumber);

    @Query("SELECT t FROM Transaction t WHERE (t.senderAccountNumber = :accountNumber OR t.receiverAccountNumber = :accountNumber) AND t.timeOfTransaction BETWEEN :startDate AND :endDate")
    List<Transaction> findByAccountNumberAndMonth(@Param("accountNumber") String accountNumber,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);
}
