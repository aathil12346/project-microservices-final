package com.bankingapp.account_services.repository;

import com.bankingapp.account_services.entity.Loan;
import com.bankingapp.account_services.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class LoanRepositoryTests {

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private UserRepository userRepository;

    private User user;
    private Loan loan;

    @BeforeEach
    public void setUp(){
         user = User.builder()
                .firstname("Aathil")
                .lastname("Meeran")
                .email("aathilmeeran46@gmail.com")
                .isEmailVerified(false)
                .role("ROLE_USER")
                .postCode("G40NT")
                .streetName("St James Road")
                .password("Aathil@123")
                .build();

         loan = Loan.builder()
                .loanType("UNSECURED")
                .loanAmount(BigDecimal.valueOf(10000))
                .loanStatus("PROCESSING")
                .termInMonths(12)
                .interestRate(12)
                .applicationTime(LocalDateTime.now())
                .user(user.getEmail())
                .build();


    }

    @Test
    public void getLoanByUserEmail(){

        User savedUser = userRepository.save(user);

        Loan savedLoan = loanRepository.save(loan);

        List<Loan> retrievedLoans = loanRepository.findLoanByUser(savedUser.getEmail());

        retrievedLoans.forEach(loan1 -> Assertions.assertThat(loan1.getUser()).isEqualTo(savedLoan.getUser()));

    }
}
