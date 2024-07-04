package com.bankingapp.transaction_services.repository;

import com.bankingapp.transaction_services.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction,String> {
}
