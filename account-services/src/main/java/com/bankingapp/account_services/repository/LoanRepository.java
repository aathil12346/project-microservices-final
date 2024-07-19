package com.bankingapp.account_services.repository;

import com.bankingapp.account_services.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan,Long> {

    Loan findLoanByUser(String username);
}
