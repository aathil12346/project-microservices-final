package com.bankingapp.account_services.repository;

import com.bankingapp.account_services.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount,Long> {

    boolean existsBankAccountsByAccountNumber(String accountNumber);

    BankAccount findBankAccountByAccountNumber(String accountNumber);
}
