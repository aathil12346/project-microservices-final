package com.bankingapp.account_services.service.impl;

import com.bankingapp.account_services.dto.BankResponseDto;
import com.bankingapp.account_services.dto.CreateBankAccountRequestDto;
import com.bankingapp.account_services.entity.BankAccount;
import com.bankingapp.account_services.entity.User;
import com.bankingapp.account_services.repository.BankAccountRepository;
import com.bankingapp.account_services.repository.UserRepository;
import com.bankingapp.account_services.service.BankAccountService;
import com.bankingapp.account_services.utils.BankAccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class BankAccountServiceImpl implements BankAccountService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Override
    public BankResponseDto createBankAccount(CreateBankAccountRequestDto requestDto) {

        Optional<User> user = userRepository.findByEmail(requestDto.getEmail());
        if (user.isPresent()){
            return BankResponseDto.builder()
                    .statusCode(BankAccountUtils.EMAIL_ALREADY_EXISTS_CODE)
                    .message(BankAccountUtils.EMAIL_ALREADY_EXISTS_MSG).build();
        }

        User newUser = new User();
        newUser.setFirstname(requestDto.getFirstname());
        newUser.setLastname(requestDto.getLastname());
        newUser.setEmail(requestDto.getEmail());
        newUser.setPassword(requestDto.getPassword());
        newUser.setPostCode(requestDto.getPostcode());
        newUser.setStreetName(requestDto.getStreetName());
        newUser.setRole("USER");
        newUser.setEmailVerified(false);

        BankAccount bankAccount = BankAccount.builder().
                accountNumber(BankAccountUtils.generateBankAccountNumber()).
                accountBalance(BigDecimal.ZERO).
                accountStatus("PENDING VERIFICATION").
                accountType(requestDto.getAccountType()).build();

        if (bankAccount.getAccountType().name().equals("CHECKING")){

            bankAccount.setInterestRate(1);
            bankAccount.setOverdraftLimit(5000);
        }else {

            bankAccount.setInterestRate(8);
            bankAccount.setOverdraftLimit(0);
        }

        newUser.getBankAccounts().add(bankAccount);
        bankAccount.setUser(newUser);

        userRepository.save(newUser);
        bankAccountRepository.save(bankAccount);

        return BankResponseDto.builder()
                .statusCode(BankAccountUtils.ACCOUNT_CREATION_REQUEST_RAISED_CODE)
                .message(BankAccountUtils.ACCOUNT_CREATION_REQUEST_RAISED_MSG)
                .build();


    }
}
