package com.bankingapp.account_services.repository;

import com.bankingapp.account_services.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan,Long> {

    List<Loan> findLoanByUser(String username);
}
