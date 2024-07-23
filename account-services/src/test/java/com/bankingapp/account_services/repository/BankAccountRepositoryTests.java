package com.bankingapp.account_services.repository;

import com.bankingapp.account_services.entity.AccountType;
import com.bankingapp.account_services.entity.BankAccount;
import com.bankingapp.account_services.entity.User;
import com.bankingapp.account_services.utils.BankAccountUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest
@ActiveProfiles("test")
public class BankAccountRepositoryTests {

    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Autowired
    private UserRepository userRepository;

    private User user;
    private BankAccount bankAccount;



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

         bankAccount = BankAccount.builder()
                .accountNumber(BankAccountUtils.generateBankAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .accountStatus("VERIFIED")
                .accountType(AccountType.valueOf("CHECKING"))
                .createdAt(LocalDateTime.now())
                .interestRate(1)
                .modifiedAt(LocalDateTime.now())
                .user(user).build();


    }

    @DisplayName(value = "Junit test for saving a bank account")
    @Test
    public void saveBankAccount(){


        userRepository.save(user);
        BankAccount savedAccount = bankAccountRepository.save(bankAccount);

        Assertions.assertThat(savedAccount).isNotNull();
        Assertions.assertThat(savedAccount.getAccountStatus()).isEqualTo("VERIFIED");


    }
    @DisplayName("Junit test for retrieving bank account information")
    @Test
    public void findBankAccountByAccountNumber(){


        userRepository.save(user);
        BankAccount savedAccount = bankAccountRepository.save(bankAccount);

        BankAccount retrievedBankAccount = bankAccountRepository.findBankAccountByAccountNumber(savedAccount.getAccountNumber());
        Assertions.assertThat(retrievedBankAccount.getAccountNumber()).isEqualTo(savedAccount.getAccountNumber());

    }

}
